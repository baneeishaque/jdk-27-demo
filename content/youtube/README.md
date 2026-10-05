# YouTube kit

A scripted, reproducible pipeline that produces the project video in **37 locales**: one
narrated video per locale that has a system voice (20), a silent subtitle-only video for the
rest (17), and a master MKV carrying one video track, every narrated locale's audio, and every
locale's subtitles.

## What exists here

| File / directory | Role |
| --- | --- |
| `translations/<locale>.json` | **Per-locale words**: slide title + narration per scene, one small file per locale (community-reviewable) |
| `build-scenes.py` | **Structure owner**: scene order, on-screen code, locale roster, voices; assembles `scenes.json` |
| `scenes.json` | **Generated** by `build-scenes.py` — the single source of truth the renderer and builder read |
| `render-slides.mjs` | Renders 1920×1080 slide PNGs with headless Chromium (per-script fonts, RTL) |
| `build-video.py` | Orchestrates TTS, subtitles, docs, slides, videos, and the master MKV |
| `script.md` | Human-readable narration script for all locales (generated) |
| `storyboard.md` | Scene-by-scene timing table (generated) |
| `subtitles/*.srt` / `*.vtt` | Upload-ready subtitles for all 37 locales (committed deliverables) |
| `metadata.md` | YouTube titles, descriptions, chapters, and tags per locale |
| `build/` | All generated media (git-ignored): audio, slides, segments, videos |

Editing model: change the words in `translations/<locale>.json`; change the structure (scenes,
code lines, roster, voices) in `build-scenes.py`; then re-run `python3 build-scenes.py` and the
pipeline. Never edit `scenes.json` by hand — it is generated.

## Locale roster

37 locales = English + 22 scheduled Indian languages + 14 major global languages. 20 have a
macOS voice and get narration; 17 are subtitle-only (no system voice ships for them).

| Group | Locales |
| --- | --- |
| Narrated (20) | `en` `bn` `hi` `kn` `ta` `te` `ar` `de` `es` `fr` `id` `it` `ja` `ko` `pt` `ru` `th` `tr` `vi` `zh` |
| Subtitle-only (17) | `as` `brx` `doi` `gu` `kok` `ks` `mai` `ml` `mni` `mr` `ne` `or` `pa` `sa` `sat` `sd` `ur` |

## Voices

| Locale | macOS voice | Notes | Locale | macOS voice | Notes |
| ------ | ----------- | ------- | ------ | ----------- | ----- |
| `en` | Aman | en_IN | `it` | Alice | it_IT |
| `bn` | Piya | bn_IN | `ja` | Kyoko | ja_JP |
| `hi` | Lekha | hi_IN | `ko` | Yuna | ko_KR |
| `kn` | Soumya | kn_IN | `pt` | Luciana | pt_BR |
| `ta` | Vani | ta_IN | `ru` | Milena | ru_RU |
| `te` | Geeta | te_IN | `th` | Kanya | th_TH |
| `ar` | Majed | ar_001 | `tr` | Yelda | tr_TR |
| `de` | Anna | de_DE | `vi` | Linh | vi_VN |
| `es` | Mónica | es_ES | `zh` | Tingting | zh_CN |
| `fr` | Thomas | fr_FR | `id` | Damayanti | id_ID |

**The subtitle-only locales:** no macOS voice ships for Assamese, Bodo, Dogri, Gujarati,
Konkani, Kashmiri, Maithili, Malayalam, Manipuri, Marathi, Nepali, Odia, Punjabi, Sanskrit,
Santali, Sindhi, or Urdu. Rather than shipping mislabeled audio, the pipeline skips their
narration with a warning and still produces their slides, subtitles, and a silent video. To
fill a gap, record a human narrator (preferred — the script is in `script.md`) or synthesize
with a cloud provider, then save the result as `build/audio/<locale>/<NN>-<scene>.wav`
(48 kHz stereo) and re-run `--videos --master`.

## Requirements

- macOS (`say`) — the only platform-specific stage; everything else is portable
- `ffmpeg` and `ffprobe` on PATH
- Node.js with the repository's dev dependencies installed (for Playwright's Chromium)
- Python 3.9+ (stdlib only)

## Running

```bash
python3 content/youtube/build-scenes.py --check             # validate translations
python3 content/youtube/build-scenes.py                     # write scenes.json
python3 content/youtube/build-video.py --all                # everything, every locale
python3 content/youtube/build-video.py --locale ar --audio  # just Arabic narration
python3 content/youtube/build-video.py --subtitles --docs   # text deliverables only
```

Stages, in dependency order: `--audio` → `--subtitles` → `--docs` → `--slides` → `--videos`
→ `--master`. Each is idempotent; re-running regenerates from `scenes.json`. A full `--all`
run over 37 locales takes on the order of hours — run it in the background and poll the log.

## Outputs

```text
build/
  audio/<locale>/NN-scene.wav        48 kHz stereo narration (narrated locales only)
  slides/<locale>/NN-scene.png       1920×1080 slides
  segments/<locale>/NN-scene.mp4     per-scene clips (identical encoder settings)
  video/<locale>.mp4                 concatenated, lossless join
  video/<locale>-subtitled.mp4       + soft mov_text subtitles
  video/master-multiaudio.mkv        1 video + 20 audio + 37 subtitle tracks, tagged
```

## Design decisions

- **Words are per-locale files; structure is code.** `translations/<locale>.json` keeps each
  language reviewable in isolation (a translator can PR one small file), while
  `build-scenes.py` owns scene order, on-screen code, and the roster. `scenes.json` is the
  generated join of the two.
- **Slides render in Chromium, not ffmpeg `drawtext`.** Chromium shapes every scheduled
  script correctly; `drawtext` renders glyph-by-glyph and breaks conjuncts and letter
  joining. Each locale pins its script font (Bangla/Devanagari/Gujarati/Gurmukhi/Oriya/
  Telugu/Tamil/Malayalam/Kannada Sangam MN, Noto Nastaliq Urdu, Noto Sans Meitei Mayek,
  Noto Sans Ol Chiki), `ar`/`ur`/`ks`/`sd` render right-to-left, and Nastaliq gets a taller
  line box.
- **Header chrome uses physical insets.** The step indicator's own `direction: ltr` (an
  isolate so "3 / 13" never reorders) makes logical insets resolve against the element, not
  the page — a logical inset collided with the brand in RTL. Physical `left`/`right` keeps
  the chrome stable in every locale.
- **Segments share identical encoder settings** (`libx264`, 30 fps, fixed GOP, AAC 48 kHz
  stereo) so the concat demuxer joins them with `-c copy` — no re-encode, no generation loss.
- **Explicit segment duration (`-t`), not `-shortest`.** With a looped image input,
  `-shortest` produced segments ~1.8 s longer than the narration; an explicit
  `lead + narration + tail` duration is deterministic.
- **Subtitles are timed from the measured narration** (not estimates) whenever audio exists,
  so the SRT matches the rendered video exactly. Voiceless locales fall back to a
  words-per-second estimate.
- **The master's audio tracks are extracted from the per-locale videos**, guaranteeing sync
  with the shared video timeline; only narrated locales contribute audio tracks, while every
  locale contributes subtitles.

## Uploading to YouTube

1. **Per-locale uploads (simplest):** upload `build/video/<locale>-subtitled.mp4` as separate
   videos (or unlisted variants), using `metadata.md` for each locale's title and description.
   Set the subtitle language from the matching `subtitles/<locale>.srt` if the embedded track
   is not picked up automatically.
2. **Single video with multi-language audio (Studio feature):** YouTube's multi-language
   audio is configured in Studio after upload (Subtitles → Add audio track). Upload
   `build/video/en.mp4` first, then add each narrated locale's audio track and subtitle file
   from `subtitles/`. Availability of multi-language audio depends on channel eligibility.
3. **Local distribution:** `master-multiaudio.mkv` plays in VLC (Audio → Audio Track,
   Subtitles → Subtitle Track) and is the artifact to archive.

## Content review before publishing

- **Native-speaker review is strongly recommended** for all 37 locales — every narration was
  machine-drafted in `translations/`. Prioritize the large-audience languages (`hi`, `bn`,
  `te`, `mr`, `gu`, `ur`, `ar`, `es`, `de`, `fr`, `pt`, `ru`, `ja`, `ko`, `zh`), then the
  remaining scheduled languages; the smaller-language drafts are best-effort and should be
  read by a speaker before they ship.
- Verify the spoken narration against the slide titles: they were written to be consistent,
  but a translator editing one side should update the other.
- The repo URL appears on every slide and in every description — find-and-replace it in
  `build-scenes.py` (META), `metadata.md`, and `content/README.md` if the repository moves.
