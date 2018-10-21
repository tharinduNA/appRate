# appRate
This is a library to show an app rate prompt to the user according to following conditions,<br /><br />
User has installed the app for 5 days.<br />
There has been 5 times the user used/opened the app.<br />
If the user decides to rate later, the app would prompt again in 5 days.<br />
Above mentioned dates are default values, you can customize as per your requirement once you initialize the library.

<br />

## Requirements
Supports Android 2.3 (Gingerbread) and upwards.

<br />

## How to use

1) Add the JitPack repository to your root build.gradle	allprojects
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2) Add the dependency
```
dependencies {
	  implementation 'com.github.tharinduNA:appRate:3a82d59238'
	}
```
3) Initialization, add one of the following lines to your Main Activity's onResume method.<br />
if you are good with default threashold values.
```
AppRateUtils.initRateApp(this);
```
<br />

```
AppRateUtils.initRateApp(this, DAYS_THRESHOLD, LAUCH_COUNT, REMIND_COUNT);
```
<br />

```
AppRateUtils.initRateApp(this, DAYS_THRESHOLD, LAUCH_COUNT, REMIND_COUNT, TITLE, MESSAGE, POSITIVE_BUTTON, NEGATIVE_BUTTON, NEUTRAL_BUTTON);
```

<br />

## Help us grow
Please use our issue [tracker](https://github.com/tharinduNA/appRate/issues) to report any problems.

<br />

## License
Copyright 2018 Tharindu Naveen Abeyratne

Licensed under the Apache License, Version 2.0 (the "License");<br />
you may not use this file except in compliance with the License.<br />
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software<br />
distributed under the License is distributed on an "AS IS" BASIS,<br />
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br />
See the License for the specific language governing permissions and<br />
limitations under the License.
