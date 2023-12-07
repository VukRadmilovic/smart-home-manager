import {RoleEnum} from "./enums/RoleEnum.ts";

export interface NewUserMultipart {
    username: string,
    email: string,
    password: string,
    profilePicture: File
    role: RoleEnum
}