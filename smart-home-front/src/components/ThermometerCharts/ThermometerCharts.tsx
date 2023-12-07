import {CssBaseline, Grid} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import {UserService} from "../../services/UserService.ts";
import {DeviceService} from "../../services/DeviceService.ts";
import React from "react";
import {PopupMessage} from "../PopupMessage/PopupMessage.tsx";
import {AxisOptions, Chart} from "react-charts";
import {ResizableBox} from "./ResizableBox.tsx";
import {ChartData} from "../../models/ChartData.ts";
import SockJsClient from 'react-stomp';
interface ThermometerChartsProps {
    userService: UserService
    deviceService: DeviceService
}

type TemperatureChartData = {
    date: Date,
    value: number,
}

type HumidityChartData = {
    date: Date,
    value: number,
}

type TemperatureSeries = {
    label: string,
    data: TemperatureChartData[]
}

type HumiditySeries = {
    label: string,
    data: HumidityChartData[]
}


export function ThermometerCharts({userService, deviceService} : ThermometerChartsProps) {
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const [primaryCursorValue, setPrimaryCursorValue] = React.useState();
    const [secondaryCursorValue, setSecondaryCursorValue] = React.useState();
    const [tempData, setTempData] = React.useState<TemperatureSeries[]>([
        {date: Date.now(), value: 55566}
    ]);
    const [humidityData, setHumidityData] = React.useState<HumiditySeries[]>([
        {date: Date.now(), value: 55566}
    ]);
    const deviceId = String(location.pathname.split('/').pop());

    const primaryTempAxis = React.useMemo(
        (): AxisOptions<TemperatureChartData> => ({
            getValue: (datum) => datum.date, }),
        []
    )

    const secondaryTempAxes = React.useMemo(
        (): AxisOptions<TemperatureChartData>[] => [
            {
                getValue: datum => datum.value,
            },
        ],
        []
    )

    const primaryHumAxis = React.useMemo(
        (): AxisOptions<HumidityChartData> => ({
            getValue: (datum) => datum.date, }),
        []
    )

    const secondaryHumAxes = React.useMemo(
        (): AxisOptions<HumidityChartData>[] => [
            {
                getValue: datum => datum.value,
            },
        ],
        []
    )
    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);
    };
    const handleWebSocketMessage = (msg : ChartData) => {
        // Handle WebSocket messages here
        console.log('Received message:', msg);
    };
    return (
        <>
            <CssBaseline/>
            <SockJsClient
                url="http://localhost:80/realtime"
                topics={['/thermometer/freshest/' + deviceId]}
                onMessage={(msg) => handleWebSocketMessage(msg)}
                ref={ (client) => { this.clientRef = client }}
            />
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
                        <ResizableBox height={100} width={200}>
                            <Chart
                                options={{
                                    data : tempData,
                                    primaryAxis: primaryTempAxis,
                                    secondaryAxes: secondaryTempAxes,
                                    primaryCursor: {
                                        value: primaryCursorValue,
                                        onChange: (value) => {
                                            setPrimaryCursorValue(value);
                                        },
                                    },
                                    secondaryCursor: {
                                        value: secondaryCursorValue,
                                        onChange: (value) => {
                                            setSecondaryCursorValue(value);
                                        },
                                    },
                                }}
                            />
                        </ResizableBox>
                        <ResizableBox height={100} width={200}>
                            <Chart
                                options={{
                                    data: humidityData,
                                    primaryAxis: primaryHumAxis,
                                    secondaryAxes: secondaryHumAxes,
                                    primaryCursor: {
                                        value: primaryCursorValue,
                                        onChange: (value) => {
                                            setPrimaryCursorValue(value);
                                        },
                                    },
                                    secondaryCursor: {
                                        value: secondaryCursorValue,
                                        onChange: (value) => {
                                            setSecondaryCursorValue(value);
                                        },
                                    },
                                }}
                            />
                        </ResizableBox>
                    </Grid>
                </Grid>
            </Grid>
            <PopupMessage message={errorMessage} isSuccess={isSuccess} handleClose={handleErrorPopupClose} open={errorPopupOpen}/>
        </>
    );
}