# YouTube upload metadata

Copy-paste metadata per locale. Durations are the rendered video lengths. Chapter
timestamps are the English timeline (~2:02); other locales shift by a few seconds — the
chapter *labels* stay valid, only the numbers need adjusting if you upload a non-English
cut as the primary video.

Full metadata (title + description + tags) is provided for the ten uploads with the
widest reach; the remaining locales get a compact table with localized titles and
durations. Every locale's subtitles are in `subtitles/`, and every narrated locale's
audio track is in the master MKV (`build/video/master-multiaudio.mkv`).

## Thumbnail

Text overlay on a dark slide: **"9 JEPs. 1 showcase."** — pair with the demo terminal
screenshot (`shot-demo.png`) so the measured numbers are visible in the thumbnail itself.

***

## English (`en`) — 2:02

### Title

```text
JDK 27: all nine JEPs, measured — 9 demos, 33 tests, real benchmarks
```

### Description

```text
JDK 27 shipped nine JEPs. This showcase measures every one of them: runnable demos,
33 tests, and real benchmarks on OpenJDK 27 (27+35), Apple Silicon.

The four free wins: G1 is now the default collector everywhere, compact object headers
(24 → 16 bytes per object), post-quantum TLS by default, and JFR redaction before disk.
The previews: structured concurrency (570 ms → 258 ms, ~2.2x), lazy constants, primitive
patterns, PEM encodings. The incubator: Vector API ~4.0x on a dot product (JMH).

This video ships with narration in 20 languages and subtitles in 37.

Code + docs + benchmark reports: https://github.com/Baneeishaque/jdk-27-demo

Chapters:
0:00 Nine JEPs, one showcase
0:10 G1 is now the default collector
0:20 Compact object headers
0:28 Post-quantum TLS
0:38 JFR redaction
0:48 Lazy constants
0:58 Primitive patterns
1:08 Structured concurrency
1:17 The measured pipeline
1:28 Vector API
1:37 PEM encodings
1:44 The receipts
1:54 Code, tests, benchmarks
```

### Tags

```text
java, jdk 27, jdk27, openjdk, structured concurrency, vector api, jmh, benchmark, g1 gc,
post-quantum, tls, jfr, pem, performance
```

***

## Other wide-reach locales

Titles are localized and upload-ready. Localize the English description using the
matching narration text in `script.md`; keep the repository link and chapter structure.

### `hi` — ~2:02

```text
नौ JEPs. एक शोकेस. — JDK 27
```

### `ar` — ~2:02

```text
تسعة JEPs. عرض واحد. — JDK 27
```

### `es` — ~2:02

```text
Nueve JEPs. Una demostración. — JDK 27
```

### `de` — ~2:02

```text
Neun JEPs. Eine Demo. — JDK 27
```

### `fr` — ~2:02

```text
Neuf JEPs. Une démonstration. — JDK 27
```

### `pt` — ~2:02

```text
Nove JEPs. Uma vitrine. — JDK 27
```

### `ja` — ~2:02

```text
9つのJEP、1つのショーケース — JDK 27
```

### `ko` — ~2:02

```text
9개의 JEP, 하나의 쇼케이스 — JDK 27
```

### `zh` — ~2:02

```text
九个 JEP，一场演示 — JDK 27
```

***

## Compact table — remaining locales

| Locale | Localized title | Duration |
| ------ | --------------- | -------- |
| `as` | নটা JEPs. এটা শ্ব'কেছ। | ~2:02 |
| `bn` | নয়টি JEPs. এক শোকেস। | ~2:02 |
| `brx` | 9 JEPs। मोनसे शोकेस। | ~2:02 |
| `doi` | नौ JEPs। इक शोकेस। | ~2:02 |
| `gu` | નવ JEPs. એક શોકેસ. | ~2:02 |
| `kn` | ಒಂಬತ್ತು JEPs. ಒಂದೇ ಶೋಕೇಸ್. | ~2:02 |
| `kok` | नोव JEPs. एक शोकेस. | ~2:02 |
| `ks` | نَو JEPs۔ اَکھ شوکیس۔ | ~2:02 |
| `mai` | नौ JEP। एक प्रदर्शन। | ~2:02 |
| `ml` | ഒൻപത് JEP-കൾ. ഒരു പ്രദർശനം. | ~2:02 |
| `mni` | ৯ JEP। অমা শোকেস। | ~2:02 |
| `mr` | नऊ JEP. एक प्रदर्शन. | ~2:02 |
| `ne` | नौ JEP. एउटै प्रदर्शन। | ~2:02 |
| `or` | ନଅଟି JEP। ଏକ ପ୍ରଦର୍ଶନୀ। | ~2:02 |
| `pa` | ਨੌਂ JEP। ਇੱਕ ਪ੍ਰਦਰਸ਼ਨੀ। | ~2:02 |
| `sa` | नव JEP। एकं प्रदर्शनम्। | ~2:02 |
| `sat` | ᱟᱨᱮᱭᱟᱜ JEP ᱾ ᱢᱤᱫᱴᱟᱝ ᱩᱫᱩᱜ ᱾ | ~2:02 |
| `sd` | نو JEPs. هڪ مظاهرو. | ~2:02 |
| `ta` | ஒன்பது JEP-கள். ஒரு காட்சி. | ~2:02 |
| `te` | తొమ్మిది JEPలు. ఒక ప్రదర్శన. | ~2:02 |
| `ur` | نو JEPs۔ ایک نمائش۔ | ~2:02 |
| `id` | Sembilan JEP. Satu demonstrasi. | ~2:02 |
| `it` | Nove JEP. Una vetrina. | ~2:02 |
| `ru` | Девять JEP. Одна витрина. | ~2:02 |
| `th` | เก้า JEP ใน showcase เดียว | ~2:02 |
| `tr` | Dokuz JEP. Tek vitrin. | ~2:02 |
| `vi` | Chín JEP. Một màn trình diễn. | ~2:02 |

Every locale's upload description should include the repository link and the chapter
labels from the English block above; the `script.md` file contains the full narration
per locale for building localized descriptions.

(zh uses Simplified Chinese.)
