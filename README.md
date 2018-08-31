# 仿今日头条自定义相册 #
## 效果图##
![效果图](http://ohdryj9ow.bkt.clouddn.com/photo.gif)

[http://ohdryj9ow.bkt.clouddn.com/photo.gif](http://ohdryj9ow.bkt.clouddn.com/photo.gif "查看不了，请点击")

## 使用方法 ##
1.在activity或者frgament实现以下接口

	public interface AcodeImgLibListener {
	    /**
	     * 获取选中的照片
	     *
	     * @param imagePhotos 相机拍照和相册库的照片集合
	     */
	    void getImagePhotos(ArrayList<ImagePhoto> imagePhotos);
	}

2.在activity或者frgament声明帮助类
 
	private IAcoderImgLibHelper acodeImgLibHelper;
	acodeImgLibHelper = new AcodeImgLibHelper(this, this,9);

3.相机调用

	acodeImgLibHelper.takePhoto();

4.相册调用

	acodeImgLibHelper.getPhotoList();

5.查看选中大图(这里可执行删除操作)
	
	acodeImgLibHelper.showBigPhoto( imagePhotos, selectPhotoData, position);

6.删除选中图片
	
	acodeImgLibHelper.remove(position);

7.必须重写以下两个方法

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        acodeImgLibHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        acodeImgLibHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        acodeImgLibHelper.release();
    }   

## 0.1版本 ##
1. 多张大图压缩oom问题
2. 纠正图片旋转角度
3. 针对后缀是jpg,jpeg,png，但却不是图片的文件处理。
4. 在ImagePhoto实体中增加了compressPath字段(压缩后的文件路径)，可直接用作上传。


## 0.2版本 ##
1. 优化照片选中后，loading卡死的情况
2. 可以设置图片的选中最大数量

## 0.3版本 ##
1. 提取出选中图片和当前展示的数据集
2. 避免了页面传参，从单利中读取

