# Unity ads for godot using new export template system

Current version of the `UnityAdsGodot.aar` and example project is available for download [here](https://drive.google.com/drive/folders/1-Hvndx5IQzCcskAWwRClNfgGxzfG--AD?usp=sharing)

## Requirements

The new export templates for android was introduced in Godot 3.2.2. So Godot 3.2.2 and up is required

## Setup

- In Godot open up the Android export template settings by going to `Project -> Export`. 
  - Select `Android` and enable costom build. 
  - Instal custom android build template. 
- Download UnityAds Android library from [here](https://github.com/Unity-Technologies/unity-ads-android/releases)
- Open up the Godots projects `android` folder and create a folder called `plugins` if it isn't there. 
- Place the `UnityAdsGodot.aar` inside the freshly created plugins folder along with `unity-ads.aar` and `UnityAdsGodot.gdap`. 
- The min SDK version will need a update so open up the `android\build\config.gradle` and update `minSdkVersion` to 26
- Last thing is to enable this plugin under `Project -> Export -> Android -> Plugins` and check the checkbox besides `Unity Ads Godot`

## Usage

```
var adsEngine = null

# Called when the node enters the scene tree for the first time.
func _ready():
	if Engine.has_singleton("UnityAdsGodot"):
		adsEngine = Engine.get_singleton("UnityAdsGodot")
		adsEngine.connect("UnityAdsReady", self, "_on_adsReady") # register callback for UnityAdsReady
		adsEngine.connect("UnityAdsFinish", self, "_on_adsFinished") # Register callback when video add is finished
        adsEngine.connect("UnityAdsError", self, "_on_adsError") # register error callback
		adsEngine.initialise("1687685", false) # App/Project id, Debug mode enabled - override
	else:
		print("Couldn't find HelloSignals singleton")

func _on_adsReady():
	print("This should be ready.")

#  Video playback finished
# 2 - UnityAds.FinishState.COMPLETED
# 1 - UnityAds.FinishState.SKIPPED
# 0 - UnityAds.FinishState.ERROR
func _on_adsFinished(reason):
	print(reason)

# Called when unity ads engine has somekind of error with string as param.
func _on_adsError(reason):
    print(reason)

func _on_showad_pressed():
	if addsEngine != null:
		if !adsEngine.isReady("rewardedVideo"):
			adsEngine.loadAd("rewardedVideo")
		else:
			adsEngine.show("rewardedVideo")
```

## Compiling from source

- Clone out this repo
- Download UnityAds Android library from [here](https://github.com/Unity-Technologies/unity-ads-android/releases) and place it inside `unityadsgodot\libs` folder
- Download corresponding version of `godot-lib.3.2.2.stable.release` from [here](https://godotengine.org/download/) and place it again in `unityadsgodot\libs` folder
- Open the project in android studio and run `gradlew build` from terminal to build the aar libs
- Build results should be located inside `unityadsgodot\build\outputs\aar` 
- Build output can be copied to Godot projects `android\plugins` alongside dependencies and plugin description file from `Godot\UnityAdsGodot.gdap`

## TODOs

- [ ] Improve documentation
- [ ] Code cleanup
- [x] Project cleanup
- [ ] Banner ads are not working and seems that api is deprecated. Need to investigate that
- [ ] Improve setup (possibly create a script for that)
- [ ] Update error code reporting to GDScript

## Known issues

- Placement id always needs to be present for some reason
- Banners are not working.