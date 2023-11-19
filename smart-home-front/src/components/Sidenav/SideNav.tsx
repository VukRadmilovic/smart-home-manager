import React from "react";
import {
    AppBar,
    Box,
    Drawer,
    Grid,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemIcon,
    ListItemText, Paper,
    Toolbar,
    Typography
} from "@mui/material";
import CollectionsIcon from '@mui/icons-material/Collections';
import LogoutIcon from '@mui/icons-material/Logout';
import MenuIcon from '@mui/icons-material/Menu';
import './SideNav.css'
import {useLocation, useNavigate} from "react-router-dom";
import {UserService} from "../../services/UserService";

interface SideNavProps {
    userService: UserService
}

export function SideNav({userService} : SideNavProps) {
    const [mobileOpen, setMobileOpen] = React.useState(false);
    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };
    const container = window !== undefined ? () => window.document.body : undefined;
    const navigate = useNavigate();
    const {pathname} = useLocation();

    const drawer = (
        <Paper className={'dark-background height-100 white-text nav-container'}>
            <Toolbar/>
            <img className={'rounded-avatar-picture'}
                 src={sessionStorage.getItem('profilePicture') as string}
                 alt={'Profile picture'}/>
            <br/>
            <Typography variant={'h4'} fontWeight={'lighter'}>{sessionStorage.getItem('username')}</Typography>
            <br/>
            <br/>
            <br/>
            <List>
                <ListItem key='/Main' disablePadding selected={pathname.includes('/Main')}>
                    <ListItemButton>
                        <ListItemIcon><CollectionsIcon className={'white-text'}/></ListItemIcon>
                        <ListItemText primary='Main'/>
                    </ListItemButton>
                </ListItem>
                <ListItem key='/RegisterProperty' disablePadding selected={pathname.includes('/RegisterProperty')}>
                    <ListItemButton>
                        <ListItemIcon><CollectionsIcon className={'white-text'}/></ListItemIcon>
                        <ListItemText primary='Register Property'/>
                    </ListItemButton>
                </ListItem>

                <ListItem key='sign-out' disablePadding className={'align-bottom center-items width-exact'}>
                    <ListItemButton onClick={() => {
                        userService.signOut();
                        navigate("/")
                    }}>
                        <ListItemIcon><LogoutIcon className={'white-text'}/></ListItemIcon>
                        <ListItemText primary='Sign Out'/>
                    </ListItemButton>
                </ListItem>
            </List>
        </Paper>
    );


    return (
        <Grid container style={{borderRight:'none'}} className={'dark-background'}>
            <AppBar
                sx={{
                    width: {xs: '100%', sm: '100%', xl: '0px', md: '0px', lg: '0px', xxl: '0px'},
                    color:'white'
                }}>
                <Toolbar className={'dark-background'}>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={handleDrawerToggle}
                        sx={{mr: 2}}>
                        <MenuIcon/>
                    </IconButton>
                </Toolbar>
            </AppBar>
            <Grid item xl={3} lg={3} md={3} sm={10} xs={12}>
                <Box
                    className={'dark-background'}
                    component="div"
                    sx={{
                        width: {
                            width: {xl: '20%', lg: '20%', md: '25%', sm: '40%', xs: '70%'},
                            border: 'none'
                        }
                    }}
                    aria-label="navbar">
                    <Drawer
                        container={container}
                        variant="temporary"
                        open={mobileOpen}
                        style={{backgroundColor:'transparent'}}
                        onClose={handleDrawerToggle}
                        ModalProps={{
                            keepMounted: true,
                        }}
                        sx={{
                            display: {xs: 'block', sm: 'block'},
                            '& .MuiDrawer-paper': {width: {xl: '20%', lg: '20%', md: '25%', sm: '40%', xs: '70%'},backgroundColor:'#0f1924',
                                borderRight:0, borderBottomRightRadius:'1em', borderTopRightRadius:'1em'},
                        }}>
                        {drawer}
                    </Drawer>
                    <Drawer
                        variant="permanent"
                        sx={{
                            display: {xs: 'none', sm: 'none', md: 'block'},
                            '& .MuiDrawer-paper': {width: {xl: '20%', lg: '20%', md: '25%', sm: '40%', xs: '70%',borderRight:0,
                                backgroundColor: '#0f1924'}},
                        }}
                        open>
                        {drawer}
                    </Drawer>
                </Box>
            </Grid>
        </Grid>
    );
}