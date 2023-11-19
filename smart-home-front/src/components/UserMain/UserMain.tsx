import {UserService} from "../../services/UserService.ts";
import {CssBaseline, Grid} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";

interface UserMainProps {
    userService: UserService,
}

export function UserMain({userService}: UserMainProps) {
    return (
        <>
            <CssBaseline/>
            <Grid container className={'dark-background'} height={'100%'}  justifyContent={'flex-start'}>
                <Grid item xs={0} sm={0} md={2} lg={2} xl={2}>
                    <SideNav userService={userService} isAdmin={false} isSuperadmin={false}/>
                </Grid>
                <Grid item height={'100%'}  xl={10} lg={10} md={10} sm={12} xs={12}
                      p={2}
                      className={'white-background'}
                      style={{borderRadius:'1.5em', overflowY:'scroll'}}
                      alignItems={'flex-start'}
                      ml={{xl: '20%', lg: '20%', md: '25%', sm: '0', xs: '0'}}
                      mt={{xl: 0, lg: 0, md: 0, sm: '64px', xs: '64px'}}>
                </Grid>
            </Grid>
        </>
    );
}