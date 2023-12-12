import {
    Button, Checkbox,
    Dialog,
    DialogContent,
    DialogTitle,
    FormControlLabel,
    Grid, IconButton, Slider, Stack, styled, Switch,
    ToggleButton,
    ToggleButtonGroup,
    Typography
} from "@mui/material";
import React, {useEffect, useRef} from "react";
import LightModeIcon from '@mui/icons-material/LightMode';
import AcUnitIcon from '@mui/icons-material/AcUnit';
import AirIcon from '@mui/icons-material/Air';
import HdrAutoIcon from '@mui/icons-material/HdrAuto';
import {LocalizationProvider, MobileTimePicker } from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import CloseIcon from '@mui/icons-material/Close';
import Stomp from "stompjs";
import SockJS from 'sockjs-client';

interface AirConditionerRemoteProps {
    open: boolean,
    handleClose: () => void,
    deviceId: number,
    openSocket: boolean,
}

const AntSwitch = styled(Switch)(({ theme }) => ({
    width: 70,
    height: 25,
    padding: 0,
    display: 'flex',
    '&:active': {
        '& .MuiSwitch-thumb': {
            width: 25,
        },
        '& .MuiSwitch-switchBase.Mui-checked': {
            transform: 'translateX(30px)',
        },
    },
    '& .MuiSwitch-switchBase': {
        padding: 1,
        '&.Mui-checked': {
            transform: 'translateX(45px)',
            color: '#fff',
            '& + .MuiSwitch-track': {
                opacity: 1,
                backgroundColor: theme.palette.mode === 'dark' ? '#177ddc' : '#1890ff',
            },
        },
    },
    '& .MuiSwitch-thumb': {
        boxShadow: '0 2px 4px 0 rgb(0 35 11 / 20%)',
        width: 22,
        height: 22,
        borderRadius: 20,
        transition: theme.transitions.create(['width'], {
            duration: 200,
        }),
    },
    '& .MuiSwitch-track': {
        borderRadius: 24 / 2,
        opacity: 1,
        backgroundColor:
            theme.palette.mode === 'dark' ? 'rgba(255,255,255,.35)' : 'rgba(0,0,0,.25)',
        boxSizing: 'border-box',
    },
}));

export function AirConditionerRemote ({open,handleClose, deviceId, openSocket} : AirConditionerRemoteProps)  {
    const [defaultFanSpeedChecked, setDefaultFanSpeedChecked] = React.useState(true);
    const [healthChecked, setHealthChecked] = React.useState(false);
    const [fungusChecked, setFungusChecked] = React.useState(false);
    const [scheduledChecked, setScheduledChecked] = React.useState(false);
    const [repeatChecked, setRepeatChecked] = React.useState(false);
    const [mode, setMode] = React.useState<string | null>(null);
    const [fanSpeedDisable, setFanSpeedDisable] = React.useState<boolean>(false);
    const webChatUrl = "http://localhost:80/realtime";
    const client = useRef(null);
    const handleMode = (
        event: React.MouseEvent<HTMLElement>,
        newMode: string | null,
    ) => {
        setMode(newMode);
        if(newMode == "auto")
            setFanSpeedDisable(true);
        else
            setFanSpeedDisable(false);
    };

    const handleFanChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDefaultFanSpeedChecked(event.target.checked);
    };

    const handleHealthChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setHealthChecked(event.target.checked);
    };

    const handleFungusChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setFungusChecked(event.target.checked);
    };

    const handleScheduleCheckedChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setScheduledChecked(event.target.checked);
    };

    const handleRepeatCheckedChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setRepeatChecked(event.target.checked);
    };
    const marks = [
        {value: 15, label: '15°C'},
        {value:38,label:'38°C'}
    ];

    const connectSocket = () => {
        try {
            client.current = Stomp.over(new SockJS(webChatUrl));
            client.current.connect(
                {},
                () => {
                    console.log(':::::: SOCKET CONNECTED ::::::');
                    client.current.subscribe('/ac/freshest/' + deviceId, onMessageReceived);
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

    const onMessageReceived = (payload) => {}

    useEffect(() => {
        console.log(open)
        if(!openSocket) {
            if(client.current != null) {
                client.current.disconnect(() => {})
            }
            return;
        } else {
            connectSocket();
        }
    }, [openSocket]);

    return (
        <React.Fragment>
        <Dialog
            maxWidth={'sm'}
            fullWidth={true}
            open={true}
            onClose={handleClose}>
            <DialogTitle textAlign={'center'}>Remote</DialogTitle>
            <DialogContent>
                <Grid container rowSpacing={3}>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12} mt={2}>
                        <Grid item container xs={12} sm={12} md={6} lg={6} xl={6}>
                            <Typography variant={"body1"} display={'inline'}>Current Status:&nbsp;</Typography>
                            <Typography color={'green'} display={'inline'}><b> ON</b></Typography>
                        </Grid>
                        <Grid item container xs={12} sm={12} md={6} lg={6} xl={6} justifyContent={'right'}>
                            <Typography variant={"body1"}>Current Temperature: <b>23°C</b></Typography>
                        </Grid>
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12} alignItems={'center'}>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={4}>
                            <Typography variant={"body1"} display={'inline'}>Set Temperature:</Typography>
                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={8}>
                            <Slider
                                sx={{margin:'0'}}
                                aria-label="Temperature"
                                defaultValue={23}
                                valueLabelDisplay="on"
                                step={1}
                                marks={marks}
                                min={15}
                                max={38}/>
                        </Grid>
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}
                          columnSpacing={1} alignItems={'center'}>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={3}>
                            <Typography variant={"body1"} display={'inline'}>Fan Speed:</Typography>
                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={4} alignItems={'center'}>
                            <FormControlLabel control={<Checkbox
                                disabled={fanSpeedDisable}
                                checked={defaultFanSpeedChecked}
                                onChange={handleFanChange}
                            />} label="Default" />

                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={5}>
                            <Slider
                                aria-label="Fan Speed"
                                disabled={defaultFanSpeedChecked || fanSpeedDisable}
                                defaultValue={1}
                                valueLabelDisplay="on"
                                sx={{margin:'0'}}
                                step={1}
                                marks={[{value:1,label:'1'},{value:3,label:'3'}]}
                                min={1}
                                max={3}/>
                        </Grid>
                    </Grid>

                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12} alignItems={'center'}>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={3}>
                            <Typography variant={"body1"} display={'inline'}>Set Mode:</Typography>
                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={9}>
                            <ToggleButtonGroup
                                color="primary"
                                value={mode}
                                exclusive
                                onChange={handleMode}
                                aria-label="mode">
                                <ToggleButton value="heat" aria-label="heat">
                                    <LightModeIcon/> Heat
                                </ToggleButton>
                                <ToggleButton value="cool" aria-label="cool">
                                    <AcUnitIcon/> Cool
                                </ToggleButton>
                                <ToggleButton value="dry" aria-label="dry">
                                    <AirIcon/> Dry
                                </ToggleButton>
                                <ToggleButton value="auto" aria-label="auto">
                                    <HdrAutoIcon/> Auto
                                </ToggleButton>
                            </ToggleButtonGroup>
                        </Grid>
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}
                          columnSpacing={1} alignItems={'center'} justifyContent={'center'} columnGap={3}>
                        <FormControlLabel control={<Checkbox
                            checked={healthChecked}
                            onChange={handleHealthChange}
                        />} label="Health/Ionizing Air" />
                        <FormControlLabel control={<Checkbox
                            checked={fungusChecked}
                            onChange={handleFungusChange}
                        />} label="Fungus Prevention" />
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}
                          alignItems={'center'}
                          justifyContent={'center'}>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={3}>
                            <Stack direction="row" spacing={1} alignItems="center">
                                <Typography>Off</Typography>
                                <AntSwitch defaultChecked  />
                                <Typography>On</Typography>
                            </Stack>
                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={3} ml={5}>
                            <Button color={'secondary'} variant={'contained'}>Send</Button>
                        </Grid>
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12} alignItems={'center'}
                    columnSpacing={2}>
                        <Grid item xs={12} sm={12} md={12} lg={12} xl={3}>
                            <FormControlLabel control={<Checkbox
                                checked={scheduledChecked}
                                onChange={handleScheduleCheckedChange}
                            />} label="Scheduled" />
                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={5} xl={4} mr={3}>
                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                <MobileTimePicker  label={'From'}  disabled={!scheduledChecked}/>
                            </LocalizationProvider>
                        </Grid>
                        <Grid item xs={12} sm={12} md={12} lg={5} xl={4}>
                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                <MobileTimePicker   label={'To'} disabled={!scheduledChecked}/>
                            </LocalizationProvider>
                        </Grid>
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12} justifyContent={'center'}>
                        <FormControlLabel control={<Checkbox
                            disabled={!scheduledChecked}
                            checked={repeatChecked}
                            onChange={handleRepeatCheckedChange}
                        />} label="Repeat Daily" />
                    </Grid>
                    <Grid container item xs={12} sm={12} md={12} lg={12} xl={12} mb={1}  justifyContent={'center'}>
                        <Button variant={'contained'} disabled={!scheduledChecked} color={'primary'}>Schedule Cycle</Button>
                    </Grid>

                </Grid>
            <IconButton aria-label="close" sx={{position:'absolute',top:'2px',right:'3px'}} onClick={handleClose}>
                <CloseIcon />
            </IconButton>
            </DialogContent>

        </Dialog>
        </React.Fragment>
    );
};