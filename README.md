## Current Version 
[![](https://jitpack.io/v/FrankChoo/PicturePicker.svg)](https://jitpack.io/#FrankChoo/PicturePicker)
- -x 表示使用的是 jetpack androidx 的依赖
- 若使用 AppCompat 将 '-x' 后缀去除即可

## How to integration
### Step 1
Add it in your **root build.gradle** at the end of repositories
```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```

### Step 2
Add it in your **module build.gradle** at the end of repositories
```
dependencies {
    ...
    implementation 'com.github.FrankChoo:PicturePicker:+'
    implementation 'com.android.support:appcompat-v7:27.+'
    implementation 'com.android.support:design:27.+'
    implementation 'com.android.support:recyclerview-v7:27.+'
}
```

## Preview([图片无法显示](http://note.youdao.com/noteshare?id=ee9a0d7909afc4e66b6dda57df10eda6&sub=125F838B572242DBA6B85FE66D89F77C))
### 图片裁剪
![图片裁剪.gif](https://user-gold-cdn.xitu.io/2018/8/6/1650cff2ccf5f4fa?w=282&h=500&f=gif&s=4452628)

### 权限与拍照
![权限与拍照.gif](https://user-gold-cdn.xitu.io/2018/8/6/1650cff2cfcacddc?w=282&h=500&f=gif&s=3251641)

### Material Design 动画
![Material Design 动画.gif](https://user-gold-cdn.xitu.io/2018/8/6/1650cff2cfd00353?w=282&h=500&f=gif&s=3963525)

### 共享元素跳转
![共享元素跳转.gif](https://user-gold-cdn.xitu.io/2018/8/6/1650cff2d58d7b01?w=282&h=500&f=gif&s=4602043)

## How to use
### 图片选择器(集成了拍照和裁剪)
```
// 1. Create an instance of PickerConfig.
val pickerConfig = PickerConfig.Builder()
        .setThreshold(etAlbumThreshold.text.toString().toInt())    // 一共选中的数量
        .setSpanCount(etSpanCount.text.toString().toInt())         // 每行展示的数目
        .isToolbarScrollable(true)                                 // Toolbar Behavior 动画
        .isFabScrollable(true)                                     // Fab Behavior 动画
        .setToolbarBackgroundColor( 
                ContextCompat.getColor(this, R.color.colorPrimary)
        )                                                          // Toolbar 背景设置
        .setIndicatorSolidColor(
                ContextCompat.getColor(this, R.color.colorPrimary)
        )                                                          // 选中指示器的颜色
        .setIndicatorBorderColor(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, android.R.color.white)
        )                                                          // 指示器边界的颜色
        .setPickerItemBackgroundColor(
                ContextCompat.getColor(this, android.R.color.white)
        )                                                          // 条目背景色
        .setCameraConfig(...)                                      // 设置相机配置, null 表示不启用拍照功能
        .setsetCropConfig(...)                                     // 设置裁剪配置, null 表示不启用裁剪功能
        .build()
        
// 2. Launch picture picker.
PicturePickerManager.with(context)
        // 传入我们上面构建的 pickerConfig.
        .setPickerConfig(
                // 调用 rebuild 方法, 对该实例二次编辑
                pickerConfig.rebuild()
                        ......
                        .build()
        )
        // 注入图片加载器
        .setPictureLoader { context, uri, imageView ->   
            Glide.with(context).load(uri).into(imageView)
        }
        .start { 
             it.forEach { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
```
### 相机(集成了裁剪)
```
// 1. Create an instance of CameraConfig
val cameraConfig = CameraConfig.Builder()
        .setFileProviderAuthority("$packageName.FileProvider")  // 指定 FileProvider 的 authority, 用于 7.0 获取文件 URI
        .setCameraDirectory(APP_DIRECTORY)                      // 拍照后的图片输出路径
        .setCameraQuality(80)                                   // 拍照后图片输出质量
        .setCropConfig(...)                                     // 设置裁剪配置, null 表示不启用裁剪功能
        .build()

// 2. Launch camera take.
CameraRequestManager.with(context)
        .setConfig(
            // 对 cameraConfig 进行二次编辑
            cameraConfig.rebuild()
                .build()
        )
        .take { takePath ->
            Toast.makeText(this, takePath, Toast.LENGTH_SHORT).show()
        }
```  
### 裁剪
```
// 1. Create an instance of CropConfig
val cropConfig = CropConfig.Builder()
        .setFileProviderAuthority("$packageName.FileProvider")   // 指定 FileProvider 的 authority, 用于 7.0 获取文件 URI
        .setCropDirectory(APP_DIRECTORY)                         // 裁剪后的图片输出路径
        .setCropSize(1000, 1000)                                 // 裁剪框的尺寸
        .setCropQuality(80)                                      // 裁剪后图片输出质量
        .build()

// 2. Launch crop page.
PictureCropManager.with((Context) mView)
        .setConfig(
            // 对 cropConfig 进行二次编辑
            cropConfig.rebuild()
                .build()
        )
        .crop(this);
```
### 图片查看器
```
// 1. Create an instance of WatcherConfig
val watcherConfig = WatcherConfig.Builder()
        .setThreshold(mPickerConfig.getThreshold())                      // 图片查看器可选图片最大数量
        .setIndicatorTextColor(mPickerConfig.getIndicatorTextColor())    // 指示器文本颜色
        .setIndicatorSolidColor(mPickerConfig.getIndicatorSolidColor())  // 指示器填充颜色
        .setIndicatorBorderColor(
                mPickerConfig.getIndicatorBorderCheckedColor(),          // 指示器边框选中的颜色
                mPickerConfig.getIndicatorBorderUncheckedColor()         // 指示器边框未选中颜色
        )
        .setPictureUris(mModel.getDisplayPaths(), position)              // 要展示的图片集合
        .setUserPickedSet(mModel.getPickedPaths())                       // 已经选中的图片集合, 传 null, 表示不支持图片选取功能
        .build()

// 2. Launch picture watcher.
PictureWatcherManager.with((Context) mView)
        .setSharedElement(sharedElement)                                 // 共享元素动画
        .setPictureLoader(PictureLoader.getPictureLoader())              // 图片加载器
        .setConfig(
            // 配置二次编辑
            watcherConfig.rebuild()
                .setPictureUris(mModel.getDisplayPaths(), position)
                .build()
        )
        .startForResult(this)/.start();
    
```
