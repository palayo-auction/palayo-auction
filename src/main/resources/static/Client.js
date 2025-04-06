import { initializeApp } from "https://www.gstatic.com/firebasejs/10.0.0/firebase-app.js";
import { getMessaging, getToken, onMessage } from "https://www.gstatic.com/firebasejs/10.0.0/firebase-messaging.js";

const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    projectId: "YOUR_PROJECT_ID",
    messagingSenderId: "YOUR_SENDER_ID",
    appId: "YOUR_APP_ID",
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

Notification.requestPermission().then(permission => {
    if (permission === 'granted') {
        getToken(messaging, { vapidKey: 'YOUR_VAPID_KEY' }).then(currentToken => {
            if (currentToken) {
                // 서버에 토큰 전송
                fetch('/register-token', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ token: currentToken })
                });
            }
        });
    }
});

// 포어그라운드 메시지 처리
onMessage(messaging, (payload) => {
    alert('포어그라운드 알림: ' + payload.notification.title);
});
