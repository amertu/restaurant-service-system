import {Injectable} from '@angular/core';
import * as fabric from 'fabric';
import {IconLoaderService} from './icon-loader.service';
import {IconName} from '@fortawesome/fontawesome-svg-core';

type ControlHandler = (event: fabric.TPointerEvent, transform: fabric.Transform) => boolean;

@Injectable({providedIn: 'root'})
export class CanvasControlsService {
  private readonly CONTROL_SIZE = 20;
  private readonly CONTROL_CORNER_SIZE = 12;

  constructor(private iconLoader: IconLoaderService) {
  }

  async initializeControls(): Promise<void> {
    // Preload the icons needed for the controls
    await this.iconLoader.preloadIcons([
      'trash', 'pen-to-square', 'power-off', 'clone'
    ]);
    this.setupDefaultControls();
  }

  private setupDefaultControls(): void {
    Object.assign(fabric.InteractiveFabricObject.ownDefaults, {
      ...fabric.InteractiveFabricObject.ownDefaults,
      controls: {
        deleteControl: this.createControl({
          iconName: 'trash',
          position: {x: 0.5, y: -0.5},
          offsetX: 16,
          offsetY: -16,
          action: this.handleDelete.bind(this),
        }),
        editControl: this.createControl({
          iconName: 'edit',
          position: {x: -0.5, y: -0.5},
          offsetX: -16,
          offsetY: -16,
          action: this.handleEdit.bind(this),
        }),
        statusControl: this.createControl({
          iconName: 'power-off',
          position: {x: 0.5, y: 0.5},
          offsetX: 16,
          offsetY: 16,
          action: this.handleStatus.bind(this),
        }),
        cloneControl: this.createControl({
          iconName: 'clone',
          position: {x: -0.5, y: 0.5},
          offsetX: -16,
          offsetY: 16,
          action: this.handleClone.bind(this),
        }),
        ...fabric.FabricText.createControls().controls,
        borderControls: new fabric.Control({
          cursorStyle: 'pointer',
          actionHandler: fabric.controlsUtils.scalingEqually,
        }),
      },
    });
  }

  private createControl(config: {
    position: { x: number; y: number };
    offsetX: number;
    offsetY: number;
    iconName: IconName;
    action: ControlHandler;
  }): fabric.Control {
    return new fabric.Control({
      x: config.position.x,
      y: config.position.y,
      offsetX: config.offsetX,
      offsetY: config.offsetY,
      cursorStyle: 'pointer',
      mouseUpHandler: config.action,
      render: this.createIconRenderer(config.iconName), // Render the icon
    });
  }


  // Icon rendering logic using the loaded icon
  private createIconRenderer(iconName: IconName): (
    ctx: CanvasRenderingContext2D,
    left: number,
    top: number,
    styleOverride: any,
    fabricObject: fabric.FabricObject
  ) => void {
    return async (ctx, left, top, _styleOverride, fabricObject) => {
      try {
        // Fetch the icon image from IconLoaderService
        const img = await this.iconLoader.getIcon(iconName);

        if (!img) throw new Error(`Icon ${iconName} not found`);

        const size = this.CONTROL_SIZE;
        ctx.save();
        ctx.translate(left, top);
        ctx.drawImage(img, -size / 2,-size / 2, size, size);
        ctx.rotate(fabric.util.degreesToRadians(fabricObject.angle));
        ctx.restore();
      } catch (error) {
        console.error('Error rendering icon:', error);
        // Fallback to a simple rectangle in case the icon fails to load
        this.renderFallbackControl(ctx, left, top);
      }
    };
  }

  // Fallback rendering in case the icon fails to load
  private renderFallbackControl(
    ctx: CanvasRenderingContext2D,
    left: number,
    top: number
  ): void {
    ctx.save();
    ctx.translate(left, top);
    ctx.beginPath();
    ctx.arc(0, 0, this.CONTROL_SIZE / 2, 0, Math.PI * 2);
    ctx.fillStyle = '#ff4444';
    ctx.fill();
    ctx.restore();
  }

  // Control event handlers
  private handleDelete(event: fabric.TPointerEvent, transform: fabric.Transform): boolean {
    transform.target.canvas?.remove(transform.target);
    return true;
  }

  private handleEdit(event: fabric.TPointerEvent, transform: fabric.Transform): boolean {
    return false;
  }

  private handleStatus(event: fabric.TPointerEvent, transform: fabric.Transform): boolean {
    const target = transform.target;
    target.set('opacity', target.opacity === 0.5 ? 1 : 0.5);
    return true;
  }

  private handleClone(event: fabric.TPointerEvent, transform: fabric.Transform): boolean {
    const canvas = transform.target.canvas;

    if (!canvas) {
      return false;
    }

    transform.target.clone().then((cloned) => {
      cloned.left += 10;
      cloned.top += 10;

      // Ensure controls are copied correctly to the cloned object
      if (!cloned.controls) {
        cloned.controls = {};
      }

      // Manually copy the controls from the original target
      cloned.controls.deleteControl = transform.target.controls.deleteControl;
      cloned.controls.cloneControl = transform.target.controls.cloneControl;

      // Add the cloned object to the canvas
      canvas.add(cloned);
    });

    return true;
  }

}
