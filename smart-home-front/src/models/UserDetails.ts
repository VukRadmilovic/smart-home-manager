import {RoleEnum} from "./enums/RoleEnum.ts";

export interface UserDetails {
    username: string,
    email: string,
    profilePicture: string
    role: RoleEnum
}