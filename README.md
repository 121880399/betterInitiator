# BetterInitiator
  [![](https://jitpack.io/v/121880399/betterInitiator.svg)](https://jitpack.io/#121880399/betterInitiator)
  
  BetterInitiator是一个android启动优化解决方案。它将最小单元定义为一个Task。可以实现Task之间的依赖并且可以指定某些初始化在特定阶段完成。
  
# 使用

第一步：在你项目的根目录下的build.gradle文件中添加如下



```css
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

第二步：添加依赖

```css
dependencies {
	        implementation 'com.github.121880399:betterInitiator:1.0.5'
	}
```

# 详细原理
参考这篇文章：https://www.jianshu.com/p/6e97b736e067
或者直接阅读源代码。有什么不足或者更好的想法欢迎给我发邮件。
# About me
就职于:首汽约车

职位:android高级工程师

Email:zhouzhengyi007@126.com

简书:http://www.jianshu.com/u/ff764c6c19e4
