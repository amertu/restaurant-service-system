import {Component, OnInit} from '@angular/core';
import {AlertService} from '../../../../services/alert.service';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {DishService} from '../../../../services/dish.service';
import {Dish} from '../../../../dtos/dish';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-dish-add',
  templateUrl: './dish-add.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./dish-add.component.scss']
})
export class DishAddComponent implements OnInit {
  submitted: boolean = false;

  constructor(private formBuilder: FormBuilder, private dishService: DishService, private alertService: AlertService) {
  }

  dishForm = this.formBuilder.group({
    name: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    category: ['', Validators.required]
  });

  ngOnInit(): void {
  }

  onSubmitAdd() {
    this.submitted = true;
    if (this.dishForm.valid) {
      const dishAdd: Dish = new Dish(null,
        this.dishForm.controls.name.value,
        Math.round(this.dishForm.controls.price.value * 100),
        this.dishForm.controls.category.value);
      this.save(dishAdd);
    }
  }

  save(dish: Dish) {
    this.dishService.createDish(dish).subscribe(
      () => {
        window.location.reload();
      },
      error => {
        this.alertService.error(error);
      }
    );
  }
}
