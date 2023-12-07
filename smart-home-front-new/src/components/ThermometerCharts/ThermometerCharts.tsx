import {CssBaseline, Grid} from "@mui/material";
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

interface ThermometerChartsProps {
    userService: UserService
    deviceService: DeviceService
}

type ChartDataShort = {
    timestamp: Date,
    value: number,
    unit: string
}

export function ThermometerCharts({userService, deviceService} : ThermometerChartsProps) {

    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const [tempData, setTempData] = React.useState<ChartDataShort[]>([]);
    const [humidityData, setHumidityData] = React.useState<ChartDataShort[]>([]);
    const deviceId = String(location.pathname.split('/').pop());
    const shouldConnect = React.useRef(true);
    const [tempLabel, setTempLabel] = React.useState<string>("Temperature (");
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

    const getMeasurement = (measurement: string) => {
        const now = new Date()
        const from = now.getHours() == 0 ? 23 : new Date().setHours(now.getHours() - 3);
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
            response.batch.forEach((value) => {
                const newVal : ChartDataShort = {
                    timestamp : new Date(value.timestamp),
                    value: value.value,
                    unit: value.tags["unit"]
                };
                data.push(newVal);
            })

            if(measurement == "temperature") {
               setTempData(data);
               setTempLabel(tempLabel + data[0].unit + ")")
            }
            else {
               setHumidityData(data);
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

    const onMessageReceived = (payload: string) => {
        //console.log(payload)
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
                        <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}>
                            <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}>
                                <ResizableBox height={300} width={1100}>
                                    <LineChart
                                        series={[
                                            { dataKey:'value', label: tempLabel},
                                        ]}
                                        xAxis={[{ scaleType:'time', dataKey:'timestamp', label: 'Time'  }]}
                                        dataset={tempData}
                                    />
                                </ResizableBox>
                            </Grid>
                        </Grid>
                        <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}>
                            <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}>
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
            </Grid>
            <PopupMessage message={errorMessage} isSuccess={isSuccess} handleClose={handleErrorPopupClose}
                          open={errorPopupOpen}/>
        </>
    );
}