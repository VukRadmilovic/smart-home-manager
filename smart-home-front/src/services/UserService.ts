import {UserCredentials} from "../models/UserCredentials.ts";
import axios from "axios";
import {Token} from "../models/Token.ts";
import {UserDetails} from "../models/UserDetails.ts";
import {NewUserMultipart} from "../models/NewUserMultipart.ts";
import {RoleEnum} from "../models/enums/RoleEnum.ts";

export class UserService {

    private api_host = "http://localhost:80"
    public async loginUser(userCredentials: UserCredentials): Promise<void> {
        try {
            const response = await axios({
                method: 'POST',
                url: `${this.api_host}/api/user/login`,
                data: userCredentials
            });
            const token: Token = response.data;
            sessionStorage.setItem("user", token.token);
            sessionStorage.setItem("expiration", String(token.expiration));
            await this.getUserData();
        } catch (err) {
            console.log(err);
            throw err;
        }
    }


    public getUserData() {
        return axios({
            method: 'GET',
            url: `${this.api_host}/api/user/info`,
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('user')
            },
        }).then((response) => {
            const userInfo: UserDetails = response.data;
            sessionStorage.setItem('username', userInfo.username);
            sessionStorage.setItem('email', userInfo.email);
            sessionStorage.setItem('profilePicture', userInfo.profilePicture);
            sessionStorage.setItem('role',userInfo.role);
            return Promise.resolve();
        }).catch((err) => {
            throw err
        });
    }

    public registerUser(newUserMultipart: NewUserMultipart): Promise<string> {
        if(newUserMultipart.role == RoleEnum.ROLE_USER) {
            return axios({
                method: 'POST',
                url: `${this.api_host}/api/user/register`,
                data: newUserMultipart,
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
            }).then((response) => response.data
            ).catch((err) => {
                throw err
            });
        }
        else {
            return axios({
                method: 'POST',
                url: `${this.api_host}/api/user/registerAdmin`,
                data: newUserMultipart,
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': 'Bearer ' + sessionStorage.getItem('user')
                },
            }).then((response) => response.data
            ).catch((err) => {
                throw err
            });
        }
    }


    public signOut(): void {
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('username');
        sessionStorage.removeItem('email');
        sessionStorage.removeItem('profilePicture');
        sessionStorage.removeItem("expiration");
        sessionStorage.removeItem("role");
    }
}