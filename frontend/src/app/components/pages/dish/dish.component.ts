import {Component, OnInit} from '@angular/core';
import {AlertService} from '../../../services/alert.service';
import {DishService} from '../../../services/dish.service';
import {Dish} from '../../../dtos/dish';
import {NgForOf} from '@angular/common';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DishAddComponent} from './dish-add/dish-add.component';
import {DishEditComponent} from './dish-edit/dish-edit.component';
import {DishDeleteComponent} from './dish-delete/dish-delete.component';

@Component({
  selector: 'app-dish-list',
  templateUrl: './dish.component.html',
  standalone: true,
  imports: [
    NgForOf,

  ],
  styleUrls: ['./dish.component.scss']
})
export class DishComponent implements OnInit {
  dishes: Dish[];

  constructor(private dishService: DishService,
              private alertService: AlertService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.loadAllDishes();
  }

  public loadAllDishes() {
    console.log('loadAllDishes()');
    this.dishService.getAllDishes().subscribe({
      next: (dishes: Dish[]) => {
        this.dishes = dishes;
      },
      error: error => {
        console.log('Failed to load dishes.');
        this.alertService.error(error);
      }
    });
  }

  formatPrice(price: number) {
    return '' + Math.floor(price / 100) + ',' + (price % 100 + ' €').padStart(4, '0');
  }

  onClickAddDish() {
    const modalRef = this.modalService.open(DishAddComponent);
    modalRef.result.then(() => this.loadAllDishes());
  }


  onClickEditDish(dish ) {
    const modalRef = this.modalService.open(DishEditComponent);
    modalRef.componentInstance.dish = dish;
    modalRef.result.then(() => this.loadAllDishes());
  }

  onClickDeleteDish(dish) {
    const modalRef = this.modalService.open(DishDeleteComponent);
    modalRef.componentInstance.dish = dish;
    modalRef.result.then(() => this.loadAllDishes());
  }
}
