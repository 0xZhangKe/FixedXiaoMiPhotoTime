# FixedXiaoMiPhotoTime
解决小米云相册下载照片到本地后文件修改时间变成当前时间问题

背景就是小米云服务 Windows 客户端下载相册照片到本地之后，文件的修改时间会被改成下载时刻的时间。

好在文件名中是包含时间信息的，但不同文件保存的方式不一样，有的是时间戳，有的是字符串等等，项目中通过正则表达式匹配时间信息，然后用这个时间修改文件的修改时间。
