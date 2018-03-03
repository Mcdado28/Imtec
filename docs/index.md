<style>
    .video-container {
        position: relative;
        padding-bottom: 56.25%;
        padding-top: 30px; height: 0; overflow: hidden;
        }

        .video-container iframe,
        .video-container object,
        .video-container embed {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
}
</style>

<div align="center">
    <img src="https://imtec.ba/img/pt-express-logo-1460547663.jpg">
</div>

# Official Imtec Android App
Application coded for Imtec Web Shop. First Android application I coded in kotlin and not in Java. Kotlin is amazing, expressive and fast. Switching application from Java to Kotlin significantly improved it's speed and shorten code size by 30% from original Java application.

<div align="center" class="video-container">
    <iframe width="620" height="349" src="https://www.youtube.com/embed/u1AsllCAQrA?feature=oembed" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen="" ></iframe>
</div>

This app supports searching product, filtering products, placing orders and much more. App has barcode scanner included so you can scann qrCodes in Imtec shops. Notifications are supported through [pushbots notifications](https://pushbots.com/).

## This app using :

- Google Analytics
- [Pushbots notifications](https://pushbots.com/)
- [Zxing Barcode Scanner](https://github.com/zxing/zxing)
- [Clans FloatingActionButton](https://github.com/Clans/FloatingActionButton)