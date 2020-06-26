# Unity ads for godot using new export template system

Current version of the `UnityAdsGodot.aar` and example project is available for download [here](https://drive.google.com/drive/folders/1-Hvndx5IQzCcskAWwRClNfgGxzfG--AD?usp=sharing)

## Requirements

The new export templates for android was introduced in godot 3.2.1, but we cannot link custom plugins as export settings panel is missing "Plugins" field so the minimum version would be 3.2.2 for this to run.

## Setup

- Open up the Android export template settings by going to `Project -> Export`. 
  - Select `Android` and enable costom build. 
  - In plugins feald type in `UnityAdsGodot`. 
- Open up the Godots projects `android` folder and create a folder called `plugins`. 
- Place the `UnityAdsGodot.aar` inside the freshly created plugins folder. 
- Download UnityAds Android library from [here](https://github.com/Unity-Technologies/unity-ads-android/releases)
- Place the downloaded `unity-ads.aar` inside `android/build/libs/plugins`
- Check if `android/build/build.gradle` does not have any filters for specific plugins and if theres a filter update the line as folows 

```
    // Godot prebuilt plugins
    implementation fileTree(dir: 'libs/plugins', include: ["*.aar"])
```

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

TODO: Describe how to do this.

## TODOs

- [ ] Improve documentation
- [ ] Code/Project cleanup
- [ ] Banner ads are not working and seems that api is deprecated. Need to investigate that
- [ ] Improve setup (possibly create a script for that)
- [ ] Update error code reporting to GDScript

## Known issues

- Placement id always needs to be present for some reason
- Banners are not working.