import {UserCredentials} from "../models/UserCredentials.ts";
import axios from "axios";
import {Token} from "../models/Token.ts";
import {UserDetails} from "../models/UserDetails.ts";
import {NewUserMultipart} from "../models/NewUserMultipart.ts";

export class UserService {

    private api_host = import.meta.env.VITE_API_HOST
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
            this.getUserData();
        } catch (err) {
            console.log(err);
            throw err;
        }
    }


    public getUserData() {
        axios({
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
            return Promise.resolve();
        }).catch((err) => {
            throw err
        });
    }

    public async registerUser(newUserMultipart: NewUserMultipart): Promise<void> {
        try {
            await axios({
                method: 'POST',
                url: `${this.api_host}/api/user/new`,
                data: newUserMultipart,
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
        } catch (err) {
            console.log(err)
            throw err;
        }
    }
}