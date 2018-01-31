### TODO LIST
---

2018-1-31:

- [x] 添加settings（设置选项）界面，添加到侧边栏
- [x] 分类名重复检测，不可以有相同的分类名


- [x] 问题：修改分类名失败后，分类列表的仍然被改掉，怀疑是`cache`被污染

原因：在`rename()`之前将`sort.Name`改掉，失败后没有将`sort.Name`改回到原来的值

解决方法：在`callbak.onError()`中改回原来的值


- [x] 问题：按时间显示界面有bug会不断闪屏，点到开关时触发

原因：`Switch`控件在`ListAdapter`中设置监听为`OnCheckChangedLinsener()`，在列表初始化时触发，导致无限循环，

解决方法：将监听方式改为`OnClickLinsener()`

- [x] 问题：设置界面设置后，再次打开并无变化，怀疑是`cache`被污染

原因：在加载设置界面之前已经执行过`GetSetting`（当时没有执行过`GetSettings`，`cache`为空），执行后写入`cache`，在加载界面时执行`GetSettings`，此时`cache`不为空，默认这是最新的数据，不读取本地存储和网络存储直接显示

解决方法：将`GetSetting`成功后写入`cache`的操作去除，`GetSetting`从网络读取成功后写入本地存储

---

2018-2-1:

- [ ] 在tasksList长度大于屏幕长度后，最下面的一条task的`Switch`控件被`FloatingActionButton`覆盖，此时应适当提高list显示高度
- [ ] 完成addtask（添加提醒）界面









