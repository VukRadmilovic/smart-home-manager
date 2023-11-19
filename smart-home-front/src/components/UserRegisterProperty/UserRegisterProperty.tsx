import {UserService} from "../../services/UserService.ts";
import {
    Button,
    CssBaseline, FormControl,
    Grid, InputLabel, MenuItem, Select, SelectChangeEvent, TextField, Typography
} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import React from "react";
import {useForm} from "react-hook-form";
import {Property} from"../../models/Property"
import {useNavigate} from "react-router-dom";
import {PropertyService} from "../../services/PropertyService";
import {PopupMessage} from "../PopupMessage/PopupMessage";
import {PropertyType} from "../../models/enums/PropertyType";

interface UserMainProps {
    userService: UserService,
}

interface PropertyForm {
    type: PropertyType,
    address: string,
    city: string,
    size: string,
    floors: string,
    status: string,
    picture: string,
}

export function UserRegisterProperty({userService}: UserMainProps, {propertyService}: PropertyService) {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const {register, handleSubmit, formState: {errors}} = useForm<PropertyForm>({
        defaultValues: {
            type: PropertyType.PROPERTY_APARTMENT,
            address: "",
            city: "",
            size: "",
            floors: "",
            status: "",
            picture: "",
        },
        mode: "onChange"
    });

    const onSubmit = (formData: PropertyForm) => tryRegisterProperty(formData)

    function tryRegisterProperty(formData : PropertyForm) {
        const property: Property = {
            address: formData.address.trim(),
            city: formData.city.trim(),
            size: formData.size.trim(),
            floors: formData.floors.trim(),
            status: formData.status.trim(),
            picture: formData.picture.trim()
        };
        propertyService.registerProperty(property).then(() => {
            navigate('/userMain');
        }).catch((error) => {
            setErrorMessage(error.response.data);
            setErrorPopupOpen(true);
        });

    }

    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);
    };
    const [city, setCity] = React.useState('');

    const handleChangeCity = (event: SelectChangeEvent) => {
        setCity(event.target.value as string);
    };

    const [type, setType] = React.useState('');

    const handleChangeType = (event: SelectChangeEvent) => {
        setType(event.target.value as string);
    };
    return (
        <>
            <CssBaseline/>
            <Grid container className={'dark-background'} height={'100%'}  justifyContent={'flex-start'}>
                <Grid item xs={0} sm={0} md={2} lg={2} xl={2}>
                    <SideNav userService={userService}/>
                </Grid>
                <Grid item height={'100%'}  xl={10} lg={10} md={10} sm={12} xs={12}
                      p={2}
                      className={'white-background'}
                      style={{borderRadius:'1.5em', overflowY:'scroll'}}
                      alignItems={'flex-start'}
                      ml={{xl: '20%', lg: '20%', md: '25%', sm: '0', xs: '0'}}
                      mt={{xl: 0, lg: 0, md: 0, sm: '64px', xs: '64px'}}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <Grid container
                              item
                              xs={12} sm={12} md={12} lg={12} xl={12}
                              direction={'row'}
                              justifyContent={"center"}>
                            <Grid item container rowSpacing={3}>
                                <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                                    <Typography variant="h2" mb={5} fontWeight={400}>Register Property</Typography>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                      justifyContent={'center'}>
                                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                        <TextField id="address"
                                                   label="Address"
                                                   fullWidth={true}
                                                   {...register("username",
                                                       {
                                                           required: "Address is a required field!",
                                                       })}
                                                   error={!!errors.address}
                                                   helperText={errors.address ? errors.address?.message : "Required"}
                                                   variant="outlined"/>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                      justifyContent={'center'}>
                                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                        <FormControl fullWidth>
                                            <InputLabel id="demo-simple-select-label">City</InputLabel>
                                            <Select
                                                labelId="demo-simple-select-label"
                                                id="demo-simple-select"
                                                value={city}
                                                label="City"
                                                onChange={handleChangeCity}
                                            >
                                                <MenuItem value={1}>Novi Sad</MenuItem>
                                                <MenuItem value={2}>Beograd</MenuItem>
                                                <MenuItem value={3}>Sombor</MenuItem>
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                      justifyContent={'center'}>
                                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                        <FormControl fullWidth>
                                            <InputLabel id="demo-simple-select-label">Property Type</InputLabel>
                                            <Select
                                                labelId="demo-simple-select-label"
                                                id="demo-simple-select"
                                                value={type}
                                                label="Type"
                                                onChange={handleChangeType}
                                            >
                                                <MenuItem value={1}>Apartmant</MenuItem>
                                                <MenuItem value={2}>House</MenuItem>
                                            </Select>
                                        </FormControl>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                      justifyContent={'center'}>
                                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                        <TextField id="size" label="Size"
                                                   fullWidth={true}
                                                   type="size"
                                                   {...register("password",
                                                       {required: "Password is a required field!"})}
                                                   error={!!errors.city}
                                                   helperText={errors.city? errors.city?.message : "Required"}
                                                   variant="outlined"/>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                      justifyContent={'center'}>
                                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                        <TextField id="floors" label="Floors"
                                                   fullWidth={true}
                                                   type="floors"
                                                   {...register("password",
                                                       {required: "Password is a required field!"})}
                                                   error={!!errors.city}
                                                   helperText={errors.city? errors.city?.message : "Required"}
                                                   variant="outlined"/>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                      justifyContent={'center'}>
                                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                        <Button variant="contained" color="secondary" component="span">
                                            Upload Image
                                        </Button>
                                    </Grid>
                                </Grid>
                                <Grid item xs={12} sm={12} md={12} lg={12} xl={12} mt={5}>
                                    <Button variant="contained" type="submit">Register Property</Button>
                                </Grid>
                            </Grid>
                        </Grid>
                    </form>
                    <PopupMessage message={errorMessage} isSuccess={false} handleClose={handleErrorPopupClose} open={errorPopupOpen}/>
                </Grid>
            </Grid>
        </>
    );
}