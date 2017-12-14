#LiveWallpaperTemplate

Inspired by [LiveWallpaper](https://github.com/WanAndroid/LiveWallPaper)

Notice:the frame animation,the frame's name must be xxxxxx_NUMBER(Example:image_1.jpg)

几个优化思路：

- Video 可考虑换用 TextureView + GL 提升性能

- Frame 可以考虑利用 HiframeAnimation 框架减少读取内存占用

- Shader 中一些计算可以转移到 Java 中使用 CPU 计算，减少开销

![Censor_Wallpaper](https://raw.githubusercontent.com/MartinRGB/LiveWallpaperTemplate/master/cover.png?raw=true)
