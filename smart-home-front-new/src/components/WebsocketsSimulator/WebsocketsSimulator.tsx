import Stomp, {Message} from "stompjs";
import SockJS from "sockjs-client";
import React, {useEffect} from "react";

export function WebsocketsSimulator() {

    const shouldConnect = React.useRef(true);
    const onMessageReceived = (payload : Message) => {
        console.log(payload);
    }


     useEffect(() => {
        if(!shouldConnect.current) return;
        shouldConnect.current = false;
        let i = 0;
        const int = setInterval(() => {
            const deviceId = Math.floor(Math.random() * (1001 - 1) + 1)
            connectSocket(deviceId.toString());
            i++;
            if(i == 1000) {
                clearInterval(int);
            }
        }, 500);

    }, []);

    const connectSocket = (deviceId: string) => {
        try {
            const webChatUrl = "http://localhost:80/realtime";
            const client = Stomp.over(new SockJS(webChatUrl));

            client.connect(
                {},
                () => {
                    console.log(':::::: SOCKET CONNECTED ::::::');
                    client.subscribe('/thermometer/freshest/' + deviceId, onMessageReceived);
                },
                () => {
                    console.log(':::::: SOCKET TRYING TO RECONNECT ::::::');
                }
            );
        } catch (err) {
            console.log(
                ':::::: ERROR: SOCKET CONNECTION ::::::' + JSON.stringify(err)
            );
        }
    };
    return (
        <></>
    );
}