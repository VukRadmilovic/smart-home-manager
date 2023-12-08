import {
    CssBaseline,
    FormControl,
    FormControlLabel,
    FormLabel,
    Grid,
    Radio,
    RadioGroup,
    Typography
} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import {UserService} from "../../services/UserService.ts";
import {DeviceService} from "../../services/DeviceService.ts";
import {PopupMessage} from "../PopupMessage/PopupMessage.tsx";
import {ResizableBox} from "./ResizableBox.tsx";
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import React, {useEffect} from "react";
import {MeasurementRequest} from "../../models/MeasurementRequest.ts";
import {LineChart} from "@mui/x-charts";
import {ChartData} from "../../models/ChartData.ts";
import {DataPoint, LTTB} from 'downsample';
import {useNavigate} from "react-router-dom";

interface ThermometerChartsProps {
    userService: UserService
    deviceService: DeviceService
}

type ChartDataShort = {
    timestamp: Date,
    value: number,
}

export function ThermometerCharts({userService, deviceService} : ThermometerChartsProps) {

    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const [tempData, setTempData] = React.useState<ChartDataShort[]>([]);
    const [humidityData, setHumidityData] = React.useState<ChartDataShort[]>([]);
    const deviceId = String(location.pathname.split('/').pop());
    const shouldConnect = React.useRef(true);
    const navigate = useNavigate();
    const [units, setUnits] = React.useState('C');

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        changeUnit((event.target as HTMLInputElement).value);
        setUnits((event.target as HTMLInputElement).value);
    };

    const connectSocket = () => {
        try {
            const webChatUrl = "http://localhost:80/realtime";
            const client = Stomp.over(new SockJS(webChatUrl));

            client.connect(
                {},
                () => {
                    console.log(':::::: SOCKET CONNECTED ::::::');
                    client.subscribe('/thermometer/freshest/' + deviceId, onMessageReceived);
                },
                () => {
                    console.log(':::::: SOCKET TRYING TO RECONNECT ::::::');
                }
            );
        } catch (err) {
            console.log(
                ':::::: ERROR: SOCKET CONNECTION ::::::' + JSON.stringify(err)
            );
        }
    };

    const changeUnit = (unit : string) => {
        if(units == unit) return;
        const transformedData: ChartDataShort[] = [];
        if(unit == "F") {
            console.log("ye")
            tempData.forEach((val) => {
                const transformed : ChartDataShort = {
                    timestamp: val.timestamp,
                    value: (val.value * 9/5) + 32
                }
                transformedData.push(transformed)
            })
        }
        else {
            console.log("c")
            tempData.forEach((val) => {
                const transformed : ChartDataShort = {
                    timestamp: val.timestamp,
                    value: (val.value - 32) * 5/9
                }
                transformedData.push(transformed)
            })
        }
        setTempData(transformedData);
    }
    const downsample = (data: ChartDataShort[], targetLength : number) => {
        const dataPoints : DataPoint[] = [];
        data.forEach((val) => {
            if(val.value != null) {
                const point: DataPoint = {
                    x: val.timestamp,
                    y: val.value
                }
                dataPoints.push(point);
            }
        })
        const res =  LTTB(dataPoints, targetLength);
        let result : ChartDataShort[] = [];
        for (let entry of res) {
            const data : ChartDataShort = {
                timestamp: entry.x,
                value: entry.y
            }
            result.push(data);
        }
       /* result.forEach((val,idx) => {
            if(idx!= result.length - 1 && result.length >= 2 &&
                (result[idx + 1].timestamp.getTime() - val.timestamp.getTime() > 60000)){
                const nullVal : ChartDataShort = {
                    timestamp: val.timestamp,
                    value: null
                }
                result.splice(idx,9,nullVal);
            }
        })*/
        return result
    }

    const getMeasurement = (measurement: string) => {
        const now = new Date()
        const from = now.getHours() == 0 ? 23 : new Date().setHours(now.getHours() - 1);
        const request : MeasurementRequest = {
            from: Math.floor(from / 1000),
            to: Math.floor(Date.now() / 1000),
            limit: 1000,
            offset: 0,
            deviceId: deviceId,
            measurementName: measurement
        }
        deviceService.getDeviceMeasurements(request).then((response => {
            if (response.batch.length == 0) {
                return;
            }
            const data : ChartDataShort[] = [];
            response.batch.forEach((value,index) => {
                const newVal : ChartDataShort = {
                    timestamp : new Date(value.timestamp),
                    value: value.value,
                };
                data.push(newVal);
                /*if(index != response.batch.length - 1 && response.batch.length >= 2 &&
                    (new Date(response.batch[index + 1].timestamp).getTime() - newVal.timestamp.getTime() > 60000))
                {
                    const nullVal: ChartDataShort = {
                        timestamp: newVal.timestamp,
                        value: null
                    }
                    data.push(nullVal)
                }*/

            })
            let downsampled : ChartDataShort[] = data;
            if(data.length > 60)
                downsampled = downsample(data,60);
            if(measurement == "temperature") {
               setTempData(downsampled);
               setUnits(response.batch[0].tags["unit"])
            }
            else {
               setHumidityData(downsampled);
            }
        })).catch((err) => {
            console.log(err);
            setErrorMessage(err.response);
            setIsSuccess(false);
            setErrorPopupOpen(true);
        });
    }

    useEffect(() => {
        if(!shouldConnect.current) return;
        connectSocket();
        getMeasurement("temperature");
        getMeasurement("humidity");
        shouldConnect.current = false;
    }, []);

    useEffect(() => {
        if (sessionStorage.getItem("expiration") != null) {
            setTimeout(() => {
                setErrorMessage("Session expired. Please log in again.");
                setIsSuccess(false);
                setErrorPopupOpen(true);
                setTimeout(() => navigate("/"), 5000);
            }, Number(sessionStorage.getItem("expiration")) - Date.now())
        } else {
            navigate("/")
        }
    });

    const onMessageReceived = (payload) => {
        const val : ChartData =  JSON.parse(payload.body);
        const newVal : ChartDataShort = {
            timestamp : new Date(+val.timestamp),
            value: val.value,
        };
        if(val.name == "temperature"){
            setTempData((prevTempData) => {
                if(prevTempData.length > 0 ) {
                    if (newVal.timestamp.getTime() - prevTempData[0].timestamp.getTime() > 3900000)
                        prevTempData.shift();
                    /*if (newVal.timestamp.getTime() - prevTempData[prevTempData.length - 1].timestamp.getTime() > 60000)
                    {
                        const nullVal : ChartDataShort = {
                            timestamp: val.timestamp,
                            value: null
                        }
                        prevTempData.push(nullVal)
                    }*/
                }
                return [...prevTempData, newVal];
            });
        }
        else {
            setHumidityData((prevHumData) => {
                if(prevHumData.length > 0 ) {
                    if (newVal.timestamp.getTime() - prevHumData[0].timestamp.getTime() > 3900000)
                        prevHumData.shift();
                }
                return [...prevHumData, newVal];
            });
        }
    }


    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);

    };
    return (
        <>
            <CssBaseline/>
            <Grid container
                  height={'100%'}
                  direction={'row'}
                  justifyContent={"center"}>
                <Grid container className={'dark-background'} height={'100%'} justifyContent={'flex-start'}>
                    <Grid item xs={0} sm={0} md={2} lg={2} xl={2}>
                        <SideNav userService={userService} isAdmin={false} isSuperadmin={false}/>
                    </Grid>
                    <Grid
                        item
                        container
                        direction={'row'}
                        height={'100%'}
                        xl={10}
                        lg={10}
                        md={10}
                        sm={12}
                        xs={12}
                        p={2}
                        className={'white-background'}
                        style={{
                            borderRadius: '1.5em',
                            overflowY: 'auto',
                            maxHeight: '100vh',
                        }}
                        alignItems={'flex-start'}
                        ml={{xl: '20%', lg: '20%', md: '25%', sm: '0', xs: '0'}}
                        mt={{xl: 0, lg: 0, md: 0, sm: '64px', xs: '64px'}}>
                        <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}
                              alignItems={'center'}
                              justifyContent={'center'}>
                            <Typography variant={'h6'} mr={2}>Units</Typography>
                            <FormControl>
                                <RadioGroup
                                    row
                                    aria-labelledby="units"
                                    defaultValue="C"
                                    value={units}
                                    onChange={handleChange}
                                    name="units-group">
                                    <FormControlLabel value="C" control={<Radio />} label="C" />
                                    <FormControlLabel value="F" control={<Radio />} label="F" />
                                </RadioGroup>
                            </FormControl>
                        </Grid>
                        <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}>
                            <ResizableBox height={300} width={1100}>
                                <LineChart
                                    series={[
                                        { dataKey:'value', label: ("Temperature (" + units + ")") as string},
                                    ]}
                                    xAxis={[{ scaleType:'time', dataKey:'timestamp', label: 'Time'  }]}
                                    dataset={tempData}
                                />
                            </ResizableBox>
                        </Grid>
                        <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}>
                            <ResizableBox height={300} width={1100}>
                                <LineChart
                                    series={[
                                        { dataKey:'value', label: 'Humidity (%)', color:'#59a14f' },
                                    ]}
                                    xAxis={[{ scaleType:'time', dataKey:'timestamp', label:'Time' }]}
                                    dataset={humidityData}
                                />
                            </ResizableBox>
                        </Grid>
                    </Grid>
                </Grid>
            </Grid>
            <PopupMessage message={errorMessage} isSuccess={isSuccess} handleClose={handleErrorPopupClose}
                          open={errorPopupOpen}/>
        </>
    );
}