import { Injectable } from '@angular/core';
import { library, icon as faIcon, IconName } from '@fortawesome/fontawesome-svg-core';
import { faTrash, faPenToSquare, faPowerOff, faClone } from '@fortawesome/free-solid-svg-icons';

@Injectable({ providedIn: 'root' })
export class IconLoaderService {
  private iconCache = new Map<IconName, HTMLImageElement>();

  constructor() {
    // Add icons to the library
    library.add(faTrash, faPenToSquare, faPowerOff, faClone);
  }
  // Preload the icons by loading them into the cache
  async preloadIcons(icons: IconName[]): Promise<void> {
    await Promise.all(icons.map(name => this.loadIcon(name)));
  }

  // Get the icon image from cache or load it if necessary
  async getIcon(name: IconName): Promise<HTMLImageElement> {
    if (this.iconCache.has(name)) {
      return this.iconCache.get(name)!;
    }
    return this.loadIcon(name);
  }

  // Load the icon from FontAwesome and convert it to an image
  private async loadIcon(name: IconName): Promise<HTMLImageElement> {
    return new Promise((resolve, reject) => {
      const iconDef = faIcon({ prefix: 'fas', iconName: name });
      if (!iconDef) return reject(`Icon not found: ${name}`);

      // Access the first element of the 'html' array which contains the SVG HTML
      const svgHtml = iconDef.html[0];

      // Create a Blob from the SVG HTML
      const svgBlob = new Blob([svgHtml], { type: 'image/svg+xml' });
      const url = URL.createObjectURL(svgBlob);

      // Create an image element and set its source to the Blob URL
      const img = new Image();
      img.onload = () => {
        URL.revokeObjectURL(url);  // Clean up the Blob URL once the image is loaded
        this.iconCache.set(name, img);  // Cache the image
        resolve(img);
      };
      img.onerror = () => reject(`Failed to load icon: ${name}`);
      img.src = url;
    });
  }
}
