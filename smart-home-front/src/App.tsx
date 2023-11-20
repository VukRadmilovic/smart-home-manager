import './App.css'
import {UserService} from "./services/UserService";
import {createTheme, ThemeProvider} from "@mui/material";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Login} from "./components/Login/Login";
import {AdminMain} from "./components/AdminMain/AdminMain.tsx";
import {UserMain} from "./components/UserMain/UserMain.tsx";
import {NewAdmin} from "./components/NewAdmin/NewAdmin.tsx";
import {UserRegisterProperty} from "./components/UserRegisterProperty/UserRegisterProperty";
import {PropertyService} from "./services/PropertyService";


function App() {
    const userServiceSingleton = new UserService();
    const propertyServiceSingleton = new PropertyService();

    const newColorScheme = createTheme({
        palette: {
            primary: {
                main: '#00ADB5'
            },
            secondary: {
                main: '#FF5722'
            }
        },
        components: {
            MuiButton: {
                styleOverrides: {
                    root: {
                        color:'white',
                        fontWeight:'400',
                        borderRadius:'0.6em'
                    }
                }
            },
            MuiFab: {
                styleOverrides: {
                    root: {
                        color:'white'
                    }
                }
            },
            MuiInputBase: {
                styleOverrides: {
                    root: {
                        borderRadius:'1em !important'
                    }
                }
            },
            MuiImageListItem: {
                styleOverrides: {
                    root: {
                        fontWeight:'300'
                    }
                }
            },
            MuiStepIcon: {
                styleOverrides: {
                    text: {
                        fill:'white'
                    }
                }
            },
            MuiStepLabel: {
                styleOverrides: {
                    root: {
                        fontWeight:'300',
                    },

                }
            },
            MuiDialog: {
                styleOverrides: {
                    paper: {
                        borderRadius:'1em'
                    }
                }
            },
            MuiDialogActions: {
                styleOverrides: {
                    root: {
                        paddingRight:'1.5em',
                        paddingBottom:'1em'
                    }
                }
            },

            MuiDrawer: {
                styleOverrides: {
                    root: {
                        "& .Mui-selected": {
                            backgroundColor: 'rgba(0,173, 181,0.2)',
                            width:'95%',
                            borderTopRightRadius:'1.5em',
                            borderBottomRightRadius:'1.5em',
                        },
                        "& .Mui-selected:hover": {
                            backgroundColor: 'rgba(0,173, 181,0.4)'
                        }
                    }
                }
            }
        }
    });

    return (
        <>
            <ThemeProvider theme={newColorScheme}>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<Login userService={userServiceSingleton}/>}/>
                        <Route path="/adminMain" element={<AdminMain userService={userServiceSingleton} propertyService={propertyServiceSingleton}/>}/>
                        <Route path="/userMain" element={<UserMain userService={userServiceSingleton} propertyService={propertyServiceSingleton}/>}/>
                        <Route path="/newAdmin" element={<NewAdmin userService={userServiceSingleton}/>}/>
                        <Route path="/userRegisterProperty" element={<UserRegisterProperty userService={userServiceSingleton} propertyService={propertyServiceSingleton}/>}/>
                    </Routes>
                </BrowserRouter>
            </ThemeProvider>
        </>
    )

}

export default App
