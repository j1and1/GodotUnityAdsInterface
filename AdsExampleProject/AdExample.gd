extends Node2D

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
	print(reason)
	reason = int(reason)
	print(reason)
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
