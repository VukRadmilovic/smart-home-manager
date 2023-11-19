import {UserService} from "../../services/UserService.ts";
import {
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
import React from "react";
import {render} from "react-dom";

interface UserMainProps {
    userService: UserService,
}

function createData(
    address: string,
    city: number,
    size: number,
    floors: number,
    status: number,
) {
    return { address, city, size, floors, status };
}

const rows = [
    createData('Frozen yoghurt', 159, 6.0, 24, 4.0),
    createData('Ice cream sandwich', 237, 9.0, 37, 4.3),
    createData('Eclair', 262, 16.0, 24, 6.0),
    createData('Cupcake', 305, 3.7, 67, 4.3),
    createData('Gingerbread', 356, 16.0, 49, 3.9),
];

export function UserMain({userService}: UserMainProps) {

    const position = [51.505, -0.09]

    // render(
    //     <MapContainer center={position} zoom={13} scrollWheelZoom={false}>
    //         <TileLayer
    //             attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    //             url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
    //         />
    //         <Marker position={position}>
    //             <Popup>
    //                 A pretty CSS3 popup. <br /> Easily customizable.
    //             </Popup>
    //         </Marker>
    //     </MapContainer>,
    // )
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
                    <TableContainer component={Paper}>
                        <Table sx={{ minWidth: 650 }} aria-label="simple table">
                            <TableHead>
                                <TableRow>
                                    <TableCell align="center">Adresa</TableCell>
                                    <TableCell align="center">Grad</TableCell>
                                    <TableCell align="right">Kvadratura</TableCell>
                                    <TableCell align="right">Broj spratova</TableCell>
                                    <TableCell align="right">Status</TableCell>
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
                                        <TableCell align="right">{row.city}</TableCell>
                                        <TableCell align="right">{row.size}</TableCell>
                                        <TableCell align="right">{row.floors}</TableCell>
                                        <TableCell align="right">{row.status}</TableCell>
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