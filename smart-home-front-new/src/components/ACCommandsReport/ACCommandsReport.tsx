import {Autocomplete, Button, CssBaseline, Grid, TextField, Typography} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import {UserService} from "../../services/UserService.ts";
import {DeviceService} from "../../services/DeviceService.ts";
import {DataGrid, GridColDef, GridPaginationModel, GridValueGetterParams} from "@mui/x-data-grid";
import {CommandSummary} from "../../models/CommandSummary.ts";
import React, {useEffect, useRef} from "react";
import {LocalizationProvider, MobileDateTimePicker} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {Dayjs} from "dayjs";
import {PopupMessage} from "../PopupMessage/PopupMessage.tsx";
import {useNavigate} from "react-router-dom";

interface ACCommandsReportProps {
    userService: UserService,
    deviceService: DeviceService
}

function getDate(params: GridValueGetterParams) {
    return `${new Date(params.row.timestamp).toLocaleString()}`;
}

const columns: GridColDef[] = [
    { field: 'timestamp',
        headerName: 'Date & Time',
        type:'Date',
        minWidth: 300,
        valueGetter: getDate,
        align:'center',
        headerAlign:'center',
    },
    { field: 'username',
        headerName: 'User',
        type:'string',
        minWidth: 300,
        headerClassName: 'bold-and-colored',
        cellClassName: 'bold-and-colored',
        align:'center',
        headerAlign:'center',
    },
    { field: 'command',
        headerName: 'Command',
        type:'string',
        flex: 2,
        align:'center',
        headerAlign:'center' }
];
export function ACCommandsReport({userService, deviceService} : ACCommandsReportProps) {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const deviceId = String(location.pathname.split('/').pop());
    const [commands,setCommands] = React.useState<CommandSummary[]>([]);
    const [currentPageCommands,setCurrentPageCommands] = React.useState<CommandSummary[]>([]);
    const [from, setFrom] = React.useState<Dayjs | null>(null);
    const [usernames, setUsernames] = React.useState<string[]>([]);
    const [to, setTo] = React.useState<Dayjs | null>(null);
    const firstLoad = useRef(true);
    const [isLoading, setIsLoading] = React.useState<boolean>(false);
    const [page, setPage] = React.useState<number>(0);
    const [rowCount, setRowCount] = React.useState<number>(100);
    const [prevPageSize, setPrevPageSize] = React.useState<number>(2);
    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);
    };
    useEffect(() => {
        if(!firstLoad.current) return;
        deviceService.getPaginatedCommands(+deviceId,0,
            Number.MAX_SAFE_INTEGER, 0, 100, -1)
            .then((val) => {
                setCommands(val.commands);
                setCurrentPageCommands(val.commands.slice(0,2))
                if(val.commands.length < 100)
                    setRowCount(val.commands.length)
                else
                    setRowCount(val.commands.length + 1);
            }).catch((error) => {
                console.log(error.response.data)
                setErrorMessage(error.response.data);
                setIsSuccess(false);
                setErrorPopupOpen(true);
        })
        firstLoad.current = false;
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

    const getNextPage = (model: GridPaginationModel) => {
        setIsLoading(true);
        if(commands.length <= model.page * model.pageSize) {
            setPage(page + 1);
            deviceService.getPaginatedCommands(+deviceId,0,
                Number.MAX_SAFE_INTEGER, page + 1, 100, -1)
                .then((val) => {
                    setCommands(prevState => [...prevState,...val.commands]);
                    setCurrentPageCommands(val.commands.slice(0,model.pageSize))
                    if(val.commands.length < 100)
                        setRowCount(rowCount + val.commands.length - 1);
                    else
                        setRowCount(rowCount + val.commands.length)
                    setIsLoading(false);
                }).catch((error) => {
                console.log(error.response.data)
                setErrorMessage(error.response.data);
                setIsLoading(false);
                setIsSuccess(false);
                setErrorPopupOpen(true);
            })
        }
        else {
            if(prevPageSize > model.pageSize)
                setCurrentPageCommands(currentPageCommands.slice(0,model.pageSize));
            else if(prevPageSize == model.pageSize)
                setCurrentPageCommands(commands.slice(model.page * prevPageSize, model.page * prevPageSize + model.pageSize));
            else {
                setCurrentPageCommands(commands.slice((model.page + 1) * prevPageSize, (model.page + 1) * prevPageSize + model.pageSize));
            }
            setIsLoading(false);
        }
        if(prevPageSize != model.pageSize)
            setPrevPageSize(model.pageSize);
    }


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
                    <Grid item xs={12} sm={12} md={12} lg={12} xl={12} mb={4} pl={2} pr={2} alignItems={'center'} container>
                            <Typography variant={'h6'} mr={4}>Filter:</Typography>
                        <Autocomplete
                            disablePortal
                            id="user"
                            options={usernames}
                            sx={{ width: 250, marginRight:'5em' }}
                            renderInput={(params) => <TextField {...params} label="User" />}
                        />
                        <LocalizationProvider dateAdapter={AdapterDayjs}>
                            <MobileDateTimePicker
                                disableFuture={true}
                                sx={{marginLeft: '1em'}}
                                label="From"
                                value={from}
                                onChange={(newValue) => setFrom(newValue)}
                            />
                            <MobileDateTimePicker
                                sx={{marginLeft: '1em'}}
                                disableFuture={true}
                                label="To"
                                value={to}
                                onChange={(newValue) => setTo(newValue)}
                            />
                        </LocalizationProvider>
                        <Button variant={'contained'}
                                color={'secondary'}
                                sx={{marginLeft:'auto'}}
                                size={'large'}>Filter</Button>
                    </Grid>
                    <DataGrid
                        sx={{borderRadius:'1.5em',width:'100%',
                            '&.MuiDataGrid-root--densityCompact .MuiDataGrid-cell': { py: '8px' },
                            '&.MuiDataGrid-root--densityStandard .MuiDataGrid-cell': { py: '15px' },
                            '&.MuiDataGrid-root--densityComfortable .MuiDataGrid-cell': { py: '22px' },}}
                        rows={currentPageCommands}
                        getRowHeight={() => 'auto'}
                        columnHeaderHeight={70}
                        columns={columns}
                        rowCount={rowCount}
                        loading={isLoading}
                        paginationMode="server"
                        onPaginationModelChange={getNextPage}
                        disableColumnMenu={true}
                        getRowId={(row) => row.timestamp}
                        initialState={{
                            pagination: {
                                paginationModel: { page: 0, pageSize: 2 },
                            },
                        }}
                        pageSizeOptions={[2,10,25,50,100]}
                    />
                </Grid>
            </Grid>
            <PopupMessage message={errorMessage} isSuccess={isSuccess} handleClose={handleErrorPopupClose}
                          open={errorPopupOpen}/>
        </>
    );
}