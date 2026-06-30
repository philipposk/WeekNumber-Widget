#!/usr/bin/env python3
"""Render app-icon assets for Android + iOS from the master SVGs.

Run: DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/lib python3 render_icons.py
"""
import io, os
import cairosvg
from PIL import Image, ImageDraw

HERE = os.path.dirname(os.path.abspath(__file__))
ANDROID = os.path.join(HERE, "..", "android", "app", "src", "main", "res")
IOS_ICON = os.path.join(HERE, "..", "ios", "WeekNumberWidget",
                        "Assets.xcassets", "AppIcon.appiconset")

# density bucket -> scale factor (mdpi = 1x)
DENSITIES = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}
LEGACY_BASE = 48     # dp of the legacy launcher bitmap
ADAPTIVE_BASE = 108  # dp of an adaptive layer


def svg_to_img(path, px):
    png = cairosvg.svg2png(url=path, output_width=px, output_height=px)
    return Image.open(io.BytesIO(png)).convert("RGBA")


def circular(img):
    mask = Image.new("L", img.size, 0)
    ImageDraw.Draw(mask).ellipse((0, 0, img.size[0], img.size[1]), fill=255)
    out = img.copy()
    out.putalpha(mask)
    return out


def save(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)
    print("wrote", os.path.relpath(path, HERE))


def main():
    master = os.path.join(HERE, "icon_master.svg")
    foreground = os.path.join(HERE, "icon_foreground.svg")
    monochrome = os.path.join(HERE, "icon_monochrome.svg")

    # --- iOS 1024 (no alpha) ---
    ios = svg_to_img(master, 1024).convert("RGB")
    save(ios, os.path.join(IOS_ICON, "icon_1024.png"))

    # --- Android per density ---
    big_master = svg_to_img(master, LEGACY_BASE * 4)        # 192
    big_round = circular(big_master)
    big_fg = svg_to_img(foreground, ADAPTIVE_BASE * 4)      # 432
    big_mono = svg_to_img(monochrome, ADAPTIVE_BASE * 4)

    for bucket, scale in DENSITIES.items():
        legacy_px = int(LEGACY_BASE * scale)
        adaptive_px = int(ADAPTIVE_BASE * scale)
        mip = os.path.join(ANDROID, f"mipmap-{bucket}")

        save(big_master.resize((legacy_px, legacy_px), Image.LANCZOS),
             os.path.join(mip, "ic_launcher.png"))
        save(big_round.resize((legacy_px, legacy_px), Image.LANCZOS),
             os.path.join(mip, "ic_launcher_round.png"))
        save(big_fg.resize((adaptive_px, adaptive_px), Image.LANCZOS),
             os.path.join(mip, "ic_launcher_foreground.png"))
        save(big_mono.resize((adaptive_px, adaptive_px), Image.LANCZOS),
             os.path.join(mip, "ic_launcher_monochrome.png"))


if __name__ == "__main__":
    main()
