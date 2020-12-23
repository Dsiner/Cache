# Cache for Android

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![API](https://img.shields.io/badge/API-10%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=10)
[![Readme](https://img.shields.io/badge/README-%E4%B8%AD%E6%96%87-brightgreen.svg)](https://github.com/Dsiner/Cache/blob/master/README-zh.md)

> Level 3 cache ( `memory` , `local`, `network` ) - Abstract asynchronous time-consuming operation for any object, main thread callback, chained call.

## Demo
<p>
   <img src="https://github.com/Dsiner/Resouce/blob/master/lib/Cache/cache.png" width="320" alt="Screenshot"/>
</p>

## Implementation list
- [x] `FrameCache` - Get the first frame and duration of local or network video.
- [x] `DurationCache` - Get local or network audio duration.
- [x] `ImageCache` - Get local or network image.
- [x] `CompressCache` - Image compress.

## Usage
```java
        FrameCache.with(context).load(url).placeholder(R.color.colorAccent).into(view);
        DurationCache.with(context).load(url).placeholder(0L).into(view);
        ImageCache.with(context).load(url).into(imageView);
        CompressCache.with(context).load(url).into(imageView);
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
