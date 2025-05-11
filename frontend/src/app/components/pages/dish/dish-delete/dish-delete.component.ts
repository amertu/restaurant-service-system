import {Component, Input, OnInit} from '@angular/core';
import {Dish} from '../../../../dtos/dish';
import {DishService} from '../../../../services/dish.service';
import {AlertService} from '../../../../services/alert.service';

@Component({
  selector: 'app-dish-delete',
  templateUrl: './dish-delete.component.html',
  standalone: true,
  styleUrls: ['./dish-delete.component.scss']
})
export class DishDeleteComponent implements OnInit {
  @Input() dish: Dish;

  constructor(public dishService: DishService, public alertService: AlertService) { }

  ngOnInit() {
  }

  public deleteDish(dish: Dish) {
    this.dishService.deleteDish(dish.id).subscribe(
      () => {
        window.location.reload();
      },
      error => {
        this.alertService.error(error);
      }
    );
  }
}
