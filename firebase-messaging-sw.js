importScripts("https://www.gstatic.com/firebasejs/9.6.1/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/9.6.1/firebase-messaging-compat.js");

firebase.initializeApp({
    apiKey: "AIzaSyBzZZDBwYnhAt6Vik-u-Q69p-yLb3FRhxo",
    authDomain: "palayo-auction-test.firebaseapp.com",
    projectId: "palayo-auction-test",
    storageBucket: "palayo-auction-test.firebasestorage.app",
    messagingSenderId: "841013647267",
    appId: "1:841013647267:web:f4dc5f7db738245e846c16"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
    const { title, body } = payload.notification;
    self.registration.showNotification(title, {
        body: body,
        icon: '/logo.png'
    });
});