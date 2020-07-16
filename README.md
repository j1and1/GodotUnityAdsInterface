# Unity ads for godot using new export template system

Current version of the `unityadsgodot-release.aar` is available for download under [Releases](https://github.com/j1and1/GodotUnityAdsInterface/releases)

## Requirements

The new export templates for android was introduced in Godot 3.2.2. So Godot 3.2.2 and up is required

## Setup

- In Godot open up the Android export template settings by going to `Project -> Export`. 
  - Select `Android` and enable costom build. 
  - Instal custom android build template. 
- Download UnityAds Android library from [here](https://github.com/Unity-Technologies/unity-ads-android/releases)
- Open up the Godots projects `android` folder and create a folder called `plugins` if it isn't there. 
- Place the `unityadsgodot-release.aar` inside the freshly created plugins folder along with `unity-ads.aar` and `UnityAdsGodot.gdap`. 
- The min SDK version will need a update so open up the `android\build\config.gradle` and update `minSdkVersion` to 26
- Last thing is to enable this plugin under `Project -> Export -> Android -> Plugins` and check the checkbox besides `Unity Ads Godot`

## Usage

Usage example is availible in `AdsExampleProject` in this repository but basically the code below sums it up

```
var addsEngine = null

# Called when the node enters the scene tree for the first time.
func _ready():
	if Engine.has_singleton("UnityAdsGodot"):
		addsEngine = Engine.get_singleton("UnityAdsGodot")
		addsEngine.connect("UnityAdsReady", self, "_on_adsReady")
		addsEngine.connect("UnityAdsFinish", self, "_on_adsFinished")
		addsEngine.connect("UnityAdsError", self, "_on_adsError")
		addsEngine.connect("UnityBannerLoaded", self, "_on_bannerLoaded")
		addsEngine.connect("UnityBannerError", self, "_on_bannerError")
		addsEngine.initialise("1687685", false) # project id and TestMode enabled
	else:
		print("Couldn't find HelloSignals singleton")

func _on_adsReady():
	print("video adds should be ready.")
	
func _on_adsFinished(placement, reason):
	reason = int(reason)
	if reason == 2:
		print("Completed")
	elif reason == 1:
		print("User skiped ad")
	else:
		print("Something went wrong")

func _on_adsError(reasonString):
	print(reasonString)
	
func _on_bannerLoaded():
	print("Banner loaded")
	
func _on_bannerError(reasonString):
	print(reasonString)

func _on_VideoAd_pressed():
	if addsEngine != null:
		addsEngine.loadAd("video")
		while !addsEngine.isReady("video"):
			pass # There should be another way to do this!
		
		addsEngine.show("video")

func _on_RewardedVideo_pressed():
	if addsEngine != null:
		addsEngine.loadAd("rewardedVideo")
		while !addsEngine.isReady("rewardedVideo"):
			pass # There should be another way to do this!
		
		addsEngine.show("rewardedVideo")


func _on_BannerAd_pressed():
	if addsEngine != null:
		addsEngine.showBanner("banners")
```

## Compiling from source

- Clone out this repo
- Download UnityAds Android library from [here](https://github.com/Unity-Technologies/unity-ads-android/releases) and place it inside `unityadsgodot\libs` folder
- Download corresponding version of `godot-lib.3.2.2.stable.release` or `godot-lib.3.2.2.stable.mono.release` from [here](https://godotengine.org/download/) and place it again in `unityadsgodot\libs` folder
- Open the project in android studio and run `gradlew build` from terminal to build the aar libs
- Build results should be located inside `unityadsgodot\build\outputs\aar` 
- Build output (that includes plugin description file and `unityadsgodot-release.aar`) can be copied to Godot projects `android\plugins` alongside dependencies

## TODOs

- [ ] Improve documentation
- [ ] Code cleanup
- [x] Project cleanup
- [ ] Banner ads are not working and seems that api is deprecated. Need to investigate that
- [ ] Improve setup (possibly create a script for that)
- [ ] Update error code reporting to GDScript

## Known issues

- Placement id always needs to be present for some reason
- Banners are not working. Something about `UnityAds: com.unity3d.services.core.api.Sdk.logError() (line:70) :: No fill for placement banners` internet says that it is normal?
