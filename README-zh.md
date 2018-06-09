# Cache for Android

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-10%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=10)

> 2级缓存（ `内存` 、 `存储卡` ） ——针对任意对象，抽象异步耗时操作，主线程回调，链式调用

## Demo
<p>
   <img src="https://github.com/Dsiner/Resouce/blob/master/lib/Cache/cache.png" width="320" alt="Screenshot"/>
</p>

## Implementation list
- [x] `FrameCache` - 获取本地或网络视频第一帧和时长
- [x] `DurationCache` - 获取本地或网络音频时长

## Usage
```java
        FrameCache.with(context).load(url).placeholder(R.color.colorAccent).into(view);
        DurationCache.with(context).load(url).placeholder(0L).into(view);
        ...
```

More usage see [Demo](app/src/main/java/com/d/cache/MainActivity.java)

## Licence

```txt
Copyright 2017 D

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
