import {
    Button,
    CssBaseline,
    Grid,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import {UserService} from "../../services/UserService.ts";
import {RoleEnum} from "../../models/enums/RoleEnum.ts";
import {PropertyService} from "../../services/PropertyService";

interface AdminMainProps {
    userService: UserService,
}
function createData(
    address: string,
    city: string,
    size: string,
    floors: string,
    status: string,
) {
    return { address, city, size, floors, status };
}
interface PropertyProps {
    userService: UserService,
    propertyService: PropertyService
}
const rows = [
];

export function AdminMain({userService, propertyService} : PropertyProps) {
    const properties = propertyService.getAllPoperty().then()
    properties.then(value => {
        value.forEach(function (value){
            console.log(value.address)
            rows.push(createData(value.address, value.floors, value.size, value.city, "Unapproved"))
        });
    })
    return (
        <>
            <CssBaseline/>
            <Grid container className={'dark-background'} height={'100%'}  justifyContent={'flex-start'}>
                <Grid item xs={0} sm={0} md={2} lg={2} xl={2}>
                    <SideNav userService={userService} isAdmin={true}
                             isSuperadmin={sessionStorage.getItem("role") == RoleEnum.ROLE_SUPERADMIN}/>
                </Grid>
                <Grid item height={'100%'}  xl={10} lg={10} md={10} sm={12} xs={12}
                      p={2}
                      className={'white-background'}
                      style={{borderRadius:'1.5em', overflowY:'scroll'}}
                      alignItems={'flex-start'}
                      ml={{xl: '20%', lg: '20%', md: '25%', sm: '0', xs: '0'}}
                      mt={{xl: 0, lg: 0, md: 0, sm: '64px', xs: '64px'}}>
                    <TableContainer component={Paper}>
                        <Table sx={{ minWidth: 650 }} aria-label="simple table">
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Adresa</TableCell>
                                    <TableCell align="center">Grad</TableCell>
                                    <TableCell align="center">Kvadratura</TableCell>
                                    <TableCell align="center">Broj spratova</TableCell>
                                    <TableCell align="center">Status</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {rows.map((row) => (
                                    <TableRow
                                        key={row.address}
                                        sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                                    >
                                        <TableCell component="th" scope="row">
                                            {row.address}
                                        </TableCell>
                                        <TableCell align="center">{row.city}</TableCell>
                                        <TableCell align="center">{row.size}</TableCell>
                                        <TableCell align="center">{row.floors}</TableCell>
                                        <TableCell align="center">
                                            <Button variant="contained" color="primary" component="span">
                                            Approve
                                            </Button>
                                            <Button variant="contained" color="secondary" component="span">
                                                Deny
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Grid>
            </Grid>
        </>
    );
}