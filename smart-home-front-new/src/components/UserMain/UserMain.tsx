import {UserService} from "../../services/UserService.ts";
import {
    Box, Button, Card, CardContent, CardMedia, CssBaseline, Fab,
    Grid, ImageList, Typography,
} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import BoltIcon from '@mui/icons-material/Bolt';
import Battery3BarIcon from '@mui/icons-material/Battery3Bar';
import {PropertyService} from "../../services/PropertyService";
import HomeIcon from '@mui/icons-material/Home';
import React, {useEffect, useRef} from "react";
import {PopupMessage} from "../PopupMessage/PopupMessage";
import {useNavigate} from "react-router";
import {Property} from "../../models/Property";

interface PropertyProps {
    userService: UserService,
    propertyService: PropertyService
}

/*function createData(
    address: string,
    city: string,
    size: string,
    floors: string,
    status: string,
) {
    return { address, city, size, floors, status };
}*/

export function UserMain({userService, propertyService} : PropertyProps) {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const shouldLoad = useRef(true);
    const [properties, setProperty] = React.useState<Property[]>([]);
    const [menuAnchorEl] = React.useState<null | HTMLElement>(null);
    const openMenu = !!menuAnchorEl;
    /*const handleMenuClick = (event: React.MouseEvent<HTMLElement>,deviceId: number) => {
        setMenuAnchorEl(event.currentTarget);
    };*/

    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);
    };

    const getUserDevices = async () => {
        try {
            const response = await propertyService.getProperty();
            if (response.length > 0) {
                setProperty(response);
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
    }, [propertyService, getUserDevices]);

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


    /*const properties = propertyService.getProperty().then()
    properties.then(value => {
        value.forEach(function (value){
            console.log(value.address)
            rows.push(createData(value.address, value.floors, value.size, value.city, "Unapproved"))
        });
    })*/

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
                            {properties.map((property) => (
                                <Card sx={{ display: 'flex',
                                    border:'1px solid #D3D3D3',
                                    borderRadius:'0.6em',
                                    padding:'0.5em',
                                    margin:'0.2em 0.1em',

                                    boxShadow:'none'}}
                                      key={property.id}>
                                    <Box sx={{ display: 'flex', flexDirection: 'column', width:'250px' }}>
                                        <CardContent sx={{ flex: '1 0 auto' }}>
                                            <Typography component="div" variant="h5" mb={1}>
                                                {property.name}
                                            </Typography>
                                            <Typography variant="subtitle1" alignItems={'center'} color="text.secondary" component="div">
                                                <span style={{display: 'inline-flex'}}> <HomeIcon/> {property.address} </span>
                                            </Typography>
                                            <Typography variant="subtitle1" alignItems={'center'} color="text.secondary" component="div">
                                                <span style={{display: 'inline-flex'}}>  {property.propertyType} </span>
                                            </Typography>
                                            <Typography variant="subtitle1" alignItems={'center'} color="text.secondary" component="div">
                                                <span style={{display: 'inline-flex'}}>{property.city} </span>
                                            </Typography>
                                        </CardContent>
                                        <Box sx={{ display: 'flex', justifyContent:'center', width:'100%', alignItems: 'center', pl: 1, pb: 1 }}>
                                            <Button  color={'secondary'} variant={'contained'} sx={{marginRight:'10px'}}>Share</Button>
                                        </Box>
                                    </Box>
                                    <CardMedia
                                        component="img"
                                        sx={{ width: 151 }}
                                        image={property.picture}
                                        alt="Property"
                                    />
                                </Card>
                            ))}
                        </ImageList>
                    </Grid>
                </Grid>
            </Grid>
            <PopupMessage message={errorMessage} isSuccess={isSuccess} handleClose={handleErrorPopupClose} open={errorPopupOpen}/>
        </>
    );
}