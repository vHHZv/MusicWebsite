<template>
  <div ref="playerRef"></div>
</template>

<script>
export default {
  name: "MusicPlayer"
}
</script>

<script setup>
import 'aplayer/dist/APlayer.min.css';
import APlayer from 'aplayer';
import {nextTick, onBeforeUnmount, onMounted, ref} from "vue";

const playerRef = ref()

onMounted(() => {
  // 创建播放器
  playerRef.value = new APlayer({
    container: playerRef.value,
    fixed: props.fixed,
    loop: 'all',
    order: 'list',
    volume: 0.1,
    audio: [],
    lrcType: 1,
    listFolded: true,
    listMaxHeight: '300px'
  })
})

onBeforeUnmount(() => {
  // 播放器销毁前停止播放
  playerRef.value.pause()
})

const props = defineProps({
  fixed: {
    type: Boolean,
    default: true
  },
})

defineExpose({play, pause})

nextTick(() => {
  setVolume()
  noAudio()
})

// 设置音量
function setVolume() {
  nextTick(() => {
    playerRef.value.on('volumechange', function () {
      const vol = Math.floor(playerRef.value.audio.volume * 100)
      playerRef.value.notice('音量：' + vol + '%', 1000, 1)
    })
  })
}

// 没有音频时停止播放
function noAudio() {
  nextTick(() => {
    playerRef.value.on('play', function () {
      if (playerRef.value.list.audios.length === 0) {
        setTimeout(() => playerRef.value.pause(), 200)
      }
    })
  })
}

// 添加歌曲
function play(song) {
  // 播放列表没有歌曲，直接添加并播放
  if (playerRef.value.list.audios.length === 0) {
    playerRef.value.list.add(song)
    playerRef.value.play()
    return
  }
  // 要播放的歌与当前播放的歌相同
  if (decodeURI(playerRef.value.audio.currentSrc) === song.url) {
    playerRef.value.seek(0)
    return
  }
  // 要播放的歌已在列表中存在，直接播放
  for (let key in playerRef.value.list.audios) {
    if (playerRef.value.list.audios[key].url === song.url) {
      playerRef.value.list.switch(key)
      return
    }
  }
  // 添加歌曲
  playerRef.value.list.add(song)
  playerRef.value.list.switch(playerRef.value.list.audios.length - 1)
}

// 暂停播放
function pause() {
  playerRef.value.pause()
}
</script>

<style scoped>

</style>