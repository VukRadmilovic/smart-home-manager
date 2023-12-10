// noinspection TypeScriptValidateTypes

import {UserService} from "../../services/UserService.ts";
import {
    Button, Checkbox,
    CssBaseline, FormControl, FormControlLabel, FormLabel,
    Grid, InputLabel, MenuItem, Radio, RadioGroup, Select, SelectChangeEvent, Stack, TextField, Typography
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
    type: string,
    propertyId: number,
    energySource: string,
    energyExpenditure?: number,
    measuringUnit?: string,
    picture: string,
    minTemperature?: number,
    maxTemperature?: number,
    fanSpeed?: number,
}

export function UserRegisterDevice({userService}: UserMainProps) {
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState<string>("");
    const [errorPopupOpen, setErrorPopupOpen] = React.useState<boolean>(false);
    const [isSuccess, setIsSuccess] = React.useState(true);
    const {register, handleSubmit, setValue, formState: {errors}} = useForm<DeviceForm>({
        defaultValues: {
            name: "",
            type: "thermometer",
            propertyId: 1,
            energySource: "AUTONOMOUS",
            energyExpenditure: 2,
            measuringUnit: "CELSIUS",
            picture: "",
            minTemperature: 14,
            maxTemperature: 34,
            fanSpeed: -1,
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

    const onSubmit = async (formData: DeviceForm) => {
        async function submitACRegistration(deviceFormData: FormData) {
            deviceFormData.append('fanSpeed', formData.fanSpeed);
            deviceFormData.append('minTemperature', formData.minTemperature?.toString() || '');
            deviceFormData.append('maxTemperature', formData.maxTemperature?.toString() || '');
            deviceFormData.append('cooling', cooling);
            deviceFormData.append('heating', heating);
            deviceFormData.append('dry', dry);
            deviceFormData.append('fan', fan);
            deviceFormData.append('auto', auto);
            deviceFormData.append('health', health);
            deviceFormData.append('fungusPrevention', fungusPrevention);

            await axios.post('http://localhost:80/api/devices/registerAC', deviceFormData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': 'Bearer ' + sessionStorage.getItem('user')
                },
            });
        }

        async function submitThermometerRegistration(deviceFormData: FormData) {
            deviceFormData.append('temperatureUnit', formData.measuringUnit?.toUpperCase());

            await axios.post('http://localhost:80/api/devices/registerThermo', deviceFormData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': 'Bearer ' + sessionStorage.getItem('user')
                },
            });
        }

        function initDeviceFormData() {
            const deviceFormData = new FormData();
            deviceFormData.append('name', formData.name);
            deviceFormData.append('propertyId', formData.propertyId.toString());
            deviceFormData.append('powerSource', formData.energySource.toUpperCase());
            deviceFormData.append('energyConsumption', formData.energyExpenditure?.toString() || '');
            deviceFormData.append('image', selectedImage);

            if (selectedImage.name == 'init') {
                deviceFormData.delete('image');
            }
            return deviceFormData;
        }

        try {
            const deviceFormData = initDeviceFormData();

            // check if energy expenditure is a number, otherwise return error
            if (formData.energyExpenditure && isNaN(formData.energyExpenditure)) {
                setErrorMessage('Energy expenditure must be a number!');
                setErrorPopupOpen(true);
                setIsSuccess(false);
                return;
            }

            if (deviceType == 'thermometer') {
                await submitThermometerRegistration(deviceFormData);
            } else if (deviceType == 'ac') {
                await submitACRegistration(deviceFormData);
            }

            setErrorMessage('Device registered successfully!');
            setErrorPopupOpen(true);
            setIsSuccess(true);
            setTimeout(() => {
                navigate('/devices');
            }, 1000);
        } catch (error) {
            console.log(error);
            if (error.response.data.includes('image' && 'null')) {
                setErrorMessage('Please select an image!');
            } else {
                setErrorMessage(error.response.data);
            }
            setErrorPopupOpen(true);
            setIsSuccess(false);
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

    const [deviceType, setDeviceType] = React.useState('ac');
    const [cooling, setCooling] = React.useState(false);
    const [heating, setHeating] = React.useState(false);
    const [dry, setDry] = React.useState(false);
    const [fan, setFan] = React.useState(false);
    const [auto, setAuto] = React.useState(false);
    const [health, setHealth] = React.useState(false);
    const [fungusPrevention, setFungusPrevention] = React.useState(false);

    const deviceTypeChanged = (event: SelectChangeEvent) => {
        setDeviceType(event.target.value as string);
    }

    const coolingChanged = () => {
        setCooling(!cooling);
    };

    const heatingChanged = () => {
        setHeating(!heating);
    };

    const dryChanged = () => {
        setDry(!dry);
    };

    const fanChanged = () => {
        setFan(!fan);
    };

    const autoChanged = () => {
        setAuto(!auto);
    };

    const healthChanged = () => {
        setHealth(!health);
    };

    const fungusPreventionChanged = () => {
        setFungusPrevention(!fungusPrevention);
    };

    function acForm() {
        return <div>
            {/*fan speed text field*/}
            <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                  justifyContent={'center'} marginBottom={'20px'}>
                <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                    <TextField id="fanSpeed"
                               label="Fan speed (optional)"
                               fullWidth={true}
                               type={"number"}
                               {...register("fanSpeed")}
                               variant="outlined"/>
                </Grid>
            </Grid>
            <p>Temperature:</p>
            <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                  justifyContent={'center'} marginBottom={'10px'}>
                <Grid item xs={12} sm={12} md={8} lg={8} xl={6} display="flex" gap={2}>
                    <TextField
                        id="minTemperature"
                        label="Minimum"
                        type="number"
                        InputLabelProps={{
                            shrink: true,
                        }}
                        {...register("minTemperature", {required: "Minimum temperature is a required field!"})}
                        error={!!errors.minTemperature}
                        helperText={errors.minTemperature ? errors.minTemperature?.message : "Required"}
                        fullWidth
                    />

                    <TextField
                        id="maxTemperature"
                        label="Maximum"
                        type="number"
                        InputLabelProps={{
                            shrink: true,
                        }}
                        {...register("maxTemperature", {required: "Maximum temperature is a required field!"})}
                        error={!!errors.maxTemperature}
                        helperText={errors.maxTemperature ? errors.maxTemperature?.message : "Required"}
                        fullWidth
                    />
                </Grid>
            </Grid>
            <FormControl component={"fieldset"} style={{'marginRight': '20px'}}>
                <FormLabel component={"legend"}>Cooling</FormLabel>
                <Checkbox aria-label={"cooling"} value={cooling} onChange={coolingChanged}></Checkbox>
            </FormControl>
            <FormControl component={"fieldset"} style={{'marginRight': '20px'}}>
                <FormLabel component={"legend"}>Heating</FormLabel>
                <Checkbox aria-label={"heating"} value={heating} onChange={heatingChanged}></Checkbox>
            </FormControl>
            <FormControl component={"fieldset"} style={{'marginRight': '20px'}}>
                <FormLabel component={"legend"}>Dry</FormLabel>
                <Checkbox aria-label={"dry"} value={dry} onChange={dryChanged}></Checkbox>
            </FormControl>
            <FormControl component={"fieldset"}>
                <FormLabel component={"legend"}>Fan</FormLabel>
                <Checkbox aria-label={"fan"} value={fan} onChange={fanChanged}></Checkbox>
            </FormControl>
            <br/>
            <FormControl component={"fieldset"} style={{'marginRight': '20px'}}>
                <FormLabel component={"legend"}>Auto</FormLabel>
                <Checkbox aria-label={"auto"} value={auto} onChange={autoChanged}></Checkbox>
            </FormControl>
            <FormControl component={"fieldset"} style={{'marginRight': '20px'}}>
                <FormLabel component={"legend"}>Health</FormLabel>
                <Checkbox aria-label={"health"} value={health} onChange={healthChanged}></Checkbox>
            </FormControl>
            <FormControl component={"fieldset"}>
                <FormLabel component={"legend"}>Fungus prevention</FormLabel>
                <Checkbox aria-label={"fungusPrevention"} value={fungusPrevention}
                          onChange={fungusPreventionChanged}></Checkbox>
            </FormControl>
        </div>;
    }

    function thermometerForm() {
        return <FormControl component="fieldset">
            <FormLabel component="legend">Measuring Unit</FormLabel>
            <RadioGroup
                aria-label="measuringUnit"
                value={measuringUnit}
                onChange={handleMeasuringUnitChange}
                style={{flexDirection: 'row'}}
            >
                <FormControlLabel value="celsius" control={<Radio/>} label="Celsius"/>
                <FormControlLabel value="fahrenheit" control={<Radio/>}
                                  label="Fahrenheit"/>
            </RadioGroup>
        </FormControl>;
    }

    function genericDeviceForm() {
        return <>
            <Grid item container rowSpacing={0}>
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
                      justifyContent={'center'} marginTop={'20px'} marginBottom={'20px'}>
                    <FormControl variant="outlined">
                        <InputLabel id="deviceType-label">Device Type</InputLabel>
                        <Select
                            labelId="deviceType-label"
                            id="deviceType"
                            label="Device Type"
                            defaultValue="thermometer"
                            value={deviceType}
                            onChange={deviceTypeChanged}
                        >
                            <MenuItem value="thermometer">Thermometer</MenuItem>
                            <MenuItem value="ac">Air Conditioner</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>

                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                      justifyContent={'center'} marginBottom={'10px'}>
                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                        <TextField
                            id="propertyId"
                            label="Property ID"
                            type="number"
                            fullWidth={true}
                            InputLabelProps={{
                                shrink: true,
                            }}
                            {...register("propertyId",
                                {required: "Property ID is a required field!"})}
                            error={!!errors.propertyId}
                            helperText={errors.propertyId ? errors.propertyId?.message : "Required"}/>
                    </Grid>
                </Grid>
                {/*name text field*/}
                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                      justifyContent={'center'} marginBottom={'10px'}>
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

                <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                      justifyContent={'center'}>
                    <Grid item xs={12} sm={12} md={8} lg={8} xl={6}>
                        <TextField id="energyExpenditure"
                                   label="Energy Expenditure (kWh)"
                                   type="text"
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
            <Grid item container justifyContent={'center'}>
                <Grid item container xs={12} sm={12} md={8} lg={8} xl={6}
                      style={{display: 'flex', justifyContent: 'center'}}>
                    <FormControl component="fieldset">
                        <FormLabel component="legend">Energy Source</FormLabel>
                        <RadioGroup
                            aria-label="energySource"
                            value={energySource}
                            onChange={handleEnergySourceChange}
                            style={{flexDirection: 'row'}}
                        >
                            <FormControlLabel value="autonomous" control={<Radio/>}
                                              label="Autonomous"/>
                            <FormControlLabel value="house" control={<Radio/>} label="House"/>
                        </RadioGroup>
                    </FormControl>
                </Grid>
            </Grid>
        </>;
    }

    return (
        <>
            <CssBaseline/>
            <form onSubmit={handleSubmit(onSubmit)}>
                <Grid container
                      direction={'row'}
                      justifyContent={"center"}>
                    <Grid container className={'dark-background'} height={'100%'} justifyContent={'flex-start'}>
                        <Grid item xs={0} sm={0} md={2} lg={2} xl={2}>
                            <SideNav userService={userService} isAdmin={false} isSuperadmin={false}/>
                        </Grid>
                        <Grid item height={'100%'} xl={10} lg={10} md={10} sm={12} xs={12} p={2}
                              className={'white-background'} style={{
                            borderRadius: '1.5em',
                            overflowY: 'auto',
                            maxHeight: '100vh',
                        }}
                              alignItems={'flex-start'}
                              ml={{xl: '20%', lg: '20%', md: '25%', sm: '0', xs: '0'}}
                              mt={{xl: 0, lg: 0, md: 0, sm: '64px', xs: '64px'}}>
                            <Grid item xs={12} sm={12} md={12} lg={12} xl={12}>
                                <Typography variant="h2" mb={5} fontWeight={400}>Register Device</Typography>
                            </Grid>
                            <Grid container item xs={12} sm={12} md={12} lg={12} xl={12}
                                  direction={'row'} justifyContent={"center"} marginBottom={"-2vh"}>
                                <Grid container item xs={12} md={8} lg={8} xl={6}>
                                    <Grid item xs={12} container direction={"column"} spacing={2}>
                                        {/* Left section */}
                                        {genericDeviceForm()}
                                    </Grid>
                                </Grid>
                                <Grid container item xs={12} md={4} lg={4} xl={6}>
                                    <Grid item xs={12} container direction="column" spacing={2}>
                                        {/* Right section */}
                                        <Grid item container xs={12} sm={12} md={12} lg={12} xl={12}
                                              justifyContent={'center'}>
                                            <Grid item container xs={12} sm={12} md={8} lg={8} xl={6}
                                                  style={{
                                                      display: 'flex',
                                                      justifyContent: 'center',
                                                      alignItems: 'center'
                                                  }}>
                                                {deviceType === "thermometer" && thermometerForm()}
                                                {deviceType === "ac" && acForm()}
                                            </Grid>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid item xs={12} sm={12} md={12} lg={12} xl={12} mt={5}>
                                <Button variant="contained" type="submit">Register device</Button>
                            </Grid>
                            <PopupMessage message={errorMessage} isSuccess={isSuccess}
                                          handleClose={handleErrorPopupClose} open={errorPopupOpen}/>
                        </Grid>
                    </Grid>
                </Grid>
            </form>
        </>
    );
}