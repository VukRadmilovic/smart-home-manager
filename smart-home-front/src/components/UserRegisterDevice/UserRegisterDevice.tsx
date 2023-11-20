import {UserService} from "../../services/UserService.ts";
import {
    Button,
    CssBaseline, FormControl, FormControlLabel, FormLabel,
    Grid, Radio, RadioGroup, SelectChangeEvent, Stack, TextField, Typography
} from "@mui/material";
import {SideNav} from "../Sidenav/SideNav.tsx";
import React, {useEffect, useState} from "react";
import {useForm} from "react-hook-form";
import {useNavigate} from "react-router-dom";
import {PopupMessage} from "../PopupMessage/PopupMessage";
import axios from 'axios';

interface UserMainProps {
    userService: UserService,
}

interface DeviceForm {
    name: string,
    description: string,
    type: string,
    propertyId: number,
    energySource: string,
    energyExpenditure?: number,
    measuringUnit: string,
    picture: string,
}

export function UserRegisterDevice({userService}: UserMainProps) {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const {register, handleSubmit, setValue, formState: {errors}} = useForm<DeviceForm>({
        defaultValues: {
            name: "",
            description: "",
            type: "thermometer",
            propertyId: 1,
            energySource: "AUTONOMOUS",
            energyExpenditure: 2,
            measuringUnit: "CELSIUS",
            picture: "",
        },
        mode: "onChange"
    });

    const [energySource, setEnergySource] = React.useState('autonomous');
    const [measuringUnit, setMeasuringUnit] = React.useState('celsius');

    const handleEnergySourceChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setEnergySource(event.target.value);
        setValue('energySource', event.target.value);
    };

    const handleMeasuringUnitChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setMeasuringUnit(event.target.value);
        setValue('measuringUnit', event.target.value);
    };

    const onSubmit = async (formData: DeviceForm) => {
        console.log('lol?');
        try {
            console.log(formData.measuringUnit.toUpperCase());
            const deviceFormData = new FormData();
            deviceFormData.append('name', formData.name);
            deviceFormData.append('description', formData.description);
            deviceFormData.append('type', 'thermometer');
            deviceFormData.append('propertyId', formData.propertyId.toString());
            deviceFormData.append('powerSource', formData.energySource.toUpperCase());
            deviceFormData.append('energyConsumption', formData.energyExpenditure?.toString() || '');
            deviceFormData.append('temperatureUnit', formData.measuringUnit.toUpperCase());
            deviceFormData.append('image', selectedImage);

            await axios.post('http://localhost:80/api/devices/register', deviceFormData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': 'Bearer ' + sessionStorage.getItem('user')
                },
            });

            alert('Successful!');
        } catch (error) {
            console.log(error);
            setErrorMessage(error.response.data);
            setErrorPopupOpen(true);
        }
    };

    const [selectedImage, setSelectedImage] = useState(new File([], "init"));
    const defaultPictureUrl = "https://t3.ftcdn.net/jpg/05/11/52/90/360_F_511529094_PISGWTmlfmBu1g4nocqdVKaHBnzMDWrN.jpg"
    const [imageUrl, setImageUrl] = useState(defaultPictureUrl);
    useEffect(() => {
        if (selectedImage && selectedImage.name != 'init') {
            setImageUrl(URL.createObjectURL(selectedImage));
        }
    }, [selectedImage]);

    const handleErrorPopupClose = (reason?: string) => {
        if (reason === 'clickaway') return;
        setErrorPopupOpen(false);
    };

    const [type, setType] = React.useState('');
    const position = [51.505, -0.09];
    const handleChangeType = (event: SelectChangeEvent) => {
        setType(event.target.value as string);

    };
    return (
        <>
            <CssBaseline/>
            <form onSubmit={handleSubmit(onSubmit)}>
                <Grid container
                      direction={'row'}
                      justifyContent={"center"}
                >
                    <Grid container className={'dark-background'} height={'100%'} justifyContent={'flex-start'}>
                        <Grid item xs={0} sm={0} md={2} lg={2} xl={2}>
                            <SideNav userService={userService}/>
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
                            mt={{xl: 0, lg: 0, md: 0, sm: '64px', xs: '64px'}}
                        >
                            <Grid container
                                  item
                                  xs={12} sm={12} md={12} lg={12} xl={12}
                                  direction={'row'}
                                  justifyContent={"center"}
                                  marginBottom={"-2vh"}>
                                <Grid item container rowSpacing={0}>
                                    <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                                        <Typography variant="h2" mb={5} fontWeight={400}>Register Device</Typography>
                                    </Grid>
                                    <Grid item container xs={12} sm={12} md={12} lg={12} xl={12} alignItems={'center'}
                                          justifyContent={'center'}>
                                        <Stack alignItems={'center'} spacing={1}>
                                            <img src={imageUrl} className={'img'} alt={'Profile picture'}/>
                                            <input
                                                accept="image/*"
                                                type="file"
                                                id="select-image"
                                                style={{display: "none"}}
                                                onChange={(e) => setSelectedImage(e.target!.files?.[0] as File)}
                                            />
                                            <label htmlFor="select-image">
                                                <Button variant="contained" color="secondary" component="span">
                                                    Upload Image
                                                </Button>
                                            </label>
                                        </Stack>
                                    </Grid>

                                    <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                          justifyContent={'center'} marginTop={"10px"}>
                                        <FormControl component="fieldset">
                                            <FormLabel component="legend">Device Type</FormLabel>
                                            <RadioGroup
                                                aria-label="deviceType"
                                                defaultValue="thermometer"
                                                {...register("deviceType", {required: true})}
                                                error={!!errors.deviceType}
                                                helperText={errors.deviceType ? errors.deviceType?.message : "Required"}
                                            >
                                                <FormControlLabel value="thermometer" control={<Radio/>}
                                                                  label="Thermometer"/>
                                            </RadioGroup>
                                        </FormControl>
                                    </Grid>

                                    <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                          justifyContent={'center'}>
                                        <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                            <TextField
                                                id="propertyId"
                                                label="Property ID"
                                                type="number"
                                                fullWidth={true}
                                                InputLabelProps={{
                                                    shrink: true,
                                                }}
                                                //required
                                                {...register("propertyId",
                                                    {required: "Property ID is a required field!"})}
                                                error={!!errors.propertyId}
                                                helperText={errors.propertyId ? errors.propertyId?.message : "Required"}/>
                                        </Grid>
                                    </Grid>
                                    {/*name text field*/}
                                    <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                          justifyContent={'center'}>
                                        <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                            <TextField id="name"
                                                       label="Name"
                                                       fullWidth={true}
                                                       {...register("name",
                                                           {
                                                               required: "Name is a required field!",
                                                           })}
                                                       error={!!errors.name}
                                                       helperText={errors.name ? errors.name?.message : "Required"}
                                                       variant="outlined"/>
                                        </Grid>
                                    </Grid>
                                    {/*description text field*/}
                                    <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                          justifyContent={'center'}>
                                        <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                            <TextField id="description"
                                                       label="Description"
                                                       fullWidth={true}
                                                       {...register("description",
                                                           {
                                                               required: "Description is a required field!",
                                                           })}
                                                       error={!!errors.description}
                                                       helperText={errors.description ? errors.description?.message : "Required"}
                                                       variant="outlined"/>
                                        </Grid>
                                    </Grid>

                                    <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                          justifyContent={'center'}>
                                        <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                                            <TextField id="energyExpenditure"
                                                       label="Energy Expenditure"
                                                       type="number"
                                                       fullWidth={true}
                                                       {...register("energyExpenditure",
                                                           {
                                                               required: "Energy expenditure is a required field!",
                                                           })}
                                                       error={!!errors.energyExpenditure}
                                                       helperText={errors.energyExpenditure ? errors.energyExpenditure?.message : "Required"}
                                                       variant="outlined"/>
                                        </Grid>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12} justifyContent={'center'}>
                                    <Grid item container xs={12} sm={12} md={8} lg={8} xl={6} style={{ display: 'flex', justifyContent: 'center' }}>
                                        <FormControl component="fieldset">
                                            <FormLabel component="legend">Energy Source</FormLabel>
                                            <RadioGroup
                                                aria-label="energySource"
                                                value={energySource}
                                                onChange={handleEnergySourceChange}
                                                style={{ flexDirection: 'row' }}
                                            >
                                                <FormControlLabel value="autonomous" control={<Radio />} label="Autonomous" />
                                                <FormControlLabel value="house" control={<Radio />} label="House" />
                                            </RadioGroup>
                                        </FormControl>
                                    </Grid>
                                </Grid>
                                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12} justifyContent={'center'}>
                                    <Grid item container xs={12} sm={12} md={8} lg={8} xl={6} style={{ display: 'flex', justifyContent: 'center' }}>
                                        <FormControl component="fieldset">
                                            <FormLabel component="legend">Measuring Unit</FormLabel>
                                            <RadioGroup
                                                aria-label="measuringUnit"
                                                value={measuringUnit}
                                                onChange={handleMeasuringUnitChange}
                                                style={{ flexDirection: 'row' }}
                                            >
                                                <FormControlLabel value="celsius" control={<Radio />} label="Celsius" />
                                                <FormControlLabel value="fahrenheit" control={<Radio />} label="Fahrenheit" />
                                            </RadioGroup>
                                        </FormControl>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item xs={12} sm={12} md={12} lg={12} xl={12} mt={5}>
                                <Button variant="contained" type="submit">Register Property</Button>
                            </Grid>
                            <PopupMessage message={errorMessage} isSuccess={false} handleClose={handleErrorPopupClose}
                                          open={errorPopupOpen}/>
                        </Grid>
                    </Grid>
                </Grid>
            </form>
        </>
    );
}