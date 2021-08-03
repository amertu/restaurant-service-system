import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {Dish} from '../../../../dtos/dish';
import {DishService} from '../../../../services/dish.service';
import {AlertService} from '../../../../services/alert.service';

@Component({
  selector: 'app-dish-edit',
  templateUrl: './dish-edit.component.html',
  styleUrls: ['./dish-edit.component.scss']
})
export class DishEditComponent implements OnInit, OnChanges {
  @Input() dish: Dish;
  submitted: boolean = false;

  constructor(private dishService: DishService,
              private formBuilder: FormBuilder,
              private alertService: AlertService) {
  }

  dishForm = this.formBuilder.group({
    id: null,
    name: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0)]],
    category: ['', Validators.required]
  });

  ngOnInit() {
    this.initDishFormGroup();
  }

  ngOnChanges(): void {
    this.initDishFormGroup();
  }

  onSubmitUpdate() {
    this.submitted = true;
    if (this.dishForm.valid) {
      const dishAdd: Dish = new Dish(this.dishForm.controls.id.value,
        this.dishForm.controls.name.value,
        Math.round(this.dishForm.controls.price.value * 100),
        this.dishForm.controls.category.value);
      this.dishService.updateDish(dishAdd).subscribe(
        () => {
          window.location.reload();
        },
        error => {
          this.alertService.error(error);
        });
    }
  }

  private initDishFormGroup() {
    this.dishForm = this.formBuilder.group({
      id: [this.dish.id],
      name: [this.dish.name, [Validators.required]],
      price: [this.dish.price / 100, [Validators.required, Validators.min(0)]],
      category:[this.dish.category,[Validators.required]]
    });
  }

}
