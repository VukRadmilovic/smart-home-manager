import {
    alpha,
    Box,
    Button,
    Card,
    CardContent,
    CardMedia,
    CssBaseline,
    Fab,
    Grid,
    ImageList, Menu, MenuItem, MenuProps, styled,
    Typography
} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import {UserService} from "../../services/UserService.ts";
import axios from 'axios';

import HomeIcon from '@mui/icons-material/Home';
import AddIcon from '@mui/icons-material/Add';
import TimelineIcon from '@mui/icons-material/Timeline';
import Battery3BarIcon from '@mui/icons-material/Battery3Bar';
import BoltIcon from '@mui/icons-material/Bolt';
import SsidChartIcon from '@mui/icons-material/SsidChart';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import ControlCameraIcon from '@mui/icons-material/ControlCamera';
import {useNavigate} from "react-router-dom";
import React, {useEffect, useRef} from "react";
import {DeviceService} from "../../services/DeviceService.ts";
import {DeviceDetailsDto} from "../../models/DeviceDetailsDto.ts";
import {PopupMessage} from "../PopupMessage/PopupMessage.tsx";
import {AirConditionerRemote} from "../AirConditionerRemote/AirConditionerRemote.tsx";
import AssessmentIcon from '@mui/icons-material/Assessment';

interface UserDevicesProps {
    userService: UserService
    deviceService: DeviceService
}
export function UserDevices({userService, deviceService} : UserDevicesProps) {
    const navigate = useNavigate();
    const [isRemoteOpen, setIsRemoteOpen] = React.useState<boolean>(false);
    const [remoteDeviceId, setRemoteDeviceId] = React.useState<number>(-1);
    const [openRemoteSocket, setOpenRemoteSocket] = React.useState<boolean>(false);
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const shouldLoad = useRef(true);
    const [devices, setDevices] = React.useState<DeviceDetailsDto[]>([]);
    const OptionsMenu = styled((props: MenuProps) => (
        <Menu
            elevation={0}
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
            }}
            transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
            }}
            {...props}
        />
    ))(({ theme }) => ({
        '& .MuiPaper-root': {
            borderRadius: 6,
            marginTop: theme.spacing(1),
            minWidth: 180,
            color:
                theme.palette.mode === 'light' ? 'rgb(55, 65, 81)' : theme.palette.grey[300],
            boxShadow:
                'rgb(255, 255, 255) 0px 0px 0px 0px, rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgba(0, 0, 0, 0.1) 0px 10px 15px -3px, rgba(0, 0, 0, 0.05) 0px 4px 6px -2px',
            '& .MuiMenu-list': {
                padding: '4px 0',
            },
            '& .MuiMenuItem-root': {
                '& .MuiSvgIcon-root': {
                    fontSize: 18,
                    color: theme.palette.text.secondary,
                    marginRight: theme.spacing(1.5),
                },
                '&:active': {
                    backgroundColor: alpha(
                        theme.palette.primary.main,
                        theme.palette.action.selectedOpacity,
                    ),
                },
            },
        },
    }));
    const [menuAnchorEl, setMenuAnchorEl] = React.useState<null | HTMLElement>(null);
    const openMenu = !!menuAnchorEl;
    const [activeDevice, setActiveDevice] = React.useState<number>(-1);
    const handleMenuClick = (event: React.MouseEvent<HTMLElement>,deviceId: number) => {
        setMenuAnchorEl(event.currentTarget);
        setActiveDevice(deviceId);
    };

    const openRemote = (deviceId: number) => {
        setIsRemoteOpen(true);
        setRemoteDeviceId(deviceId);
        setOpenRemoteSocket(true);
        handleMenuClose();
    }

    const handleRemoteClose = () => {
        setIsRemoteOpen(false);
        setOpenRemoteSocket(false);
    }

    const handleMenuClose = () => {
        setMenuAnchorEl(null);
        setActiveDevice(-1)
    };

    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);
    };

    const navigateToRealTimeCharts = (deviceId: number) => {
        navigate('/thermoCharts/' + deviceId);
    }

    const navigateToHistoryCharts = (deviceId: number) => {
        navigate('/thermoChartsHistory/' + deviceId);
    }

    const getUserDevices = async () => {
        try {
            const response = await deviceService.getUserDevices();
            if (response.length > 0) {
                setDevices(response);
            }
        } catch (err) {
            setErrorMessage(err.response?.data || "An error occurred while fetching devices.");
            setIsSuccess(false);
            setErrorPopupOpen(true);
        }
    };

    useEffect(() => {
        const fetchUserDevices = async () => {
            try {
                await getUserDevices();
                shouldLoad.current = false;
            } catch (err) {
                console.error("Error fetching devices:", err);
            }
        };

        if (shouldLoad.current) {
            fetchUserDevices();
        }
    }, [deviceService, getUserDevices]);

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

    async function turnSolarPanelOn(id: number) {
        await axios.put('http://localhost:80/api/devices/sps/' + id + '/on', {}, {
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        });

        setErrorMessage("Solar panel turned on.");
        setIsSuccess(true);
        setErrorPopupOpen(true);
        return;
    }

    async function turnSolarPanelOff(id: number) {
        await axios.put('http://localhost:80/api/devices/sps/' + id + '/off', {}, {
            headers: {
                'Content-Type': 'multipart/form-data',
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        });

        setErrorMessage("Solar panel turned off.");
        setIsSuccess(true);
        setErrorPopupOpen(true);
        return;
    }

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
                        <ImageList  sx={{
                            columnCount: {
                                xs: '2 !important',
                                sm: '4 !important',
                                md: '4 !important',
                                lg: '5 !important',
                                xl: '5 !important',
                            },
                            width: "100%"}} cols={3} rowHeight={164}>
                            {devices.map((device) => (
                            <Card sx={{ display: 'flex',
                                border:'1px solid #D3D3D3',
                                borderRadius:'0.6em',
                                padding:'0.5em',
                                margin:'0.2em 0.1em',

                                boxShadow:'none'}}
                                key={device.id}>
                                <Box sx={{ display: 'flex', flexDirection: 'column', width:'250px' }}>
                                    <CardContent sx={{ flex: '1 0 auto' }}>
                                        <Typography component="div" variant="h5" mb={1}>
                                            {device.name}
                                        </Typography>

                                        <Typography variant="subtitle1" alignItems={'center'} color="text.secondary" component="div">
                                            <span style={{display: 'inline-flex'}}> <HomeIcon/> {device.propertyName} </span>
                                        </Typography>
                                        <Typography variant="subtitle1" alignItems={'center'} color="text.secondary" component="div">
                                            <span style={{display: 'inline-flex'}}> <BoltIcon/>  {device.powerSource} </span>
                                        </Typography>
                                        <Typography variant="subtitle1" alignItems={'center'} color="text.secondary" component="div">
                                            <span style={{display: 'inline-flex'}}> <Battery3BarIcon/> {device.energyConsumption} kWh </span>
                                        </Typography>
                                    </CardContent>
                                    <Box sx={{ display: 'flex', justifyContent:'center', width:'100%', alignItems: 'center', pl: 1, pb: 1 }}>
                                        <Button  color={'secondary'} variant={'contained'} sx={{marginRight:'10px'}}>Share</Button>
                                        <div>
                                            <Button
                                                id={"button_" + device.id}
                                                aria-controls={openMenu ? 'menu_' + device.id : undefined}
                                                aria-haspopup="true"
                                                aria-expanded={openMenu ? 'true' : undefined}
                                                variant="contained"
                                                disableElevation
                                                onClick={(evt) => handleMenuClick(evt,device.id)}
                                                endIcon={<KeyboardArrowDownIcon />}>
                                                More
                                            </Button>
                                            {device.id != activeDevice ? null :
                                            <OptionsMenu
                                                MenuListProps={{
                                                    'aria-labelledby': 'button_' + device.id,
                                                }}
                                                anchorEl={menuAnchorEl}
                                                open={openMenu}
                                                onClose={handleMenuClose}>
                                                {device.type != "THERMOMETER" ? null :
                                                <MenuItem onClick={() => navigateToRealTimeCharts(device.id)} disableRipple>
                                                    <SsidChartIcon />
                                                    Real-Time Monitoring
                                                </MenuItem>
                                                }
                                                {device.type != "THERMOMETER" ? null :
                                                <MenuItem onClick={() => navigateToHistoryCharts(device.id)} disableRipple>
                                                    <TimelineIcon />
                                                        History Monitoring
                                                </MenuItem>
                                                }
                                                {device.type == "THERMOMETER" ? null :
                                                <MenuItem onClick={() => openRemote(device.id)} disableRipple>
                                                    <ControlCameraIcon />
                                                    Control
                                                </MenuItem>
                                                }
                                                {device.type == "THERMOMETER" ? null :
                                                    <MenuItem onClick={() => navigate("/acCommands/" + device.id)} disableRipple>
                                                        <AssessmentIcon />
                                                        Commands History
                                                    </MenuItem>
                                                }
                                                {device.type == "THERMOMETER" ? null :
                                                    <MenuItem onClick={() => turnSolarPanelOn(device.id)} disableRipple>
                                                        On
                                                    </MenuItem>
                                                }
                                                {device.type == "THERMOMETER" ? null :
                                                    <MenuItem onClick={() => turnSolarPanelOff(device.id)} disableRipple>
                                                        Off
                                                    </MenuItem>
                                                }
                                            </OptionsMenu>
                                        }
                                        </div>
                                    </Box>
                                </Box>
                                <CardMedia
                                    component="img"
                                    sx={{ width: 151 }}
                                    image={device.picture}
                                    alt="Thermo"
                                />
                            </Card>
                                ))}
                        </ImageList>
                    </Grid>
                    <Fab variant="extended"
                         color="primary"
                         sx={{position: 'absolute', bottom: 16, right: 30}}
                         onClick={() => navigate("/userRegisterDevice")}>
                        <AddIcon sx={{ mr: 1 }} />
                        Add New
                    </Fab>
                </Grid>
            </Grid>
            <PopupMessage message={errorMessage} isSuccess={isSuccess} handleClose={handleErrorPopupClose} open={errorPopupOpen}/>
            <AirConditionerRemote open={isRemoteOpen}
                                  handleClose={handleRemoteClose}
                                  openSocket={openRemoteSocket}
                                  deviceId={remoteDeviceId}></AirConditionerRemote>
        </>
    );
}