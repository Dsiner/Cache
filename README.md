# Cache for Android

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Cache 2级缓存（内存、存储卡） ——针对任意对象，抽象异步耗时操作，主线程回调，使用方式等同Glide

## Usage
```java
        FrameCache.with(context).load(url).placeholder(null).into(preView);
        DurationCache.with(context).load(url).placeholder(0).into(duraionView);
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
