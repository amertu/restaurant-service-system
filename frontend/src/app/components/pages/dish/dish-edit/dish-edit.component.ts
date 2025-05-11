import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Dish} from '../../../../dtos/dish';
import {DishService} from '../../../../services/dish.service';
import {AlertService} from '../../../../services/alert.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-dish-edit',
  templateUrl: './dish-edit.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf
  ],
  styleUrls: ['./dish-edit.component.scss']
})
export class DishEditComponent implements OnInit, OnChanges {
  @Input() dish: Dish;
  submitted: boolean = false;

  constructor(private dishService: DishService,
              private formBuilder: FormBuilder,
              private alertService: AlertService) {
  }

  dishForm: FormGroup<{
    id: FormControl<number | null>;
    name: FormControl<string>;
    price: FormControl<number>;
    category: FormControl<string>;
  }>;

  ngOnInit() {
    this.initDishFormGroup();
  }

  ngOnChanges(): void {
    this.initDishFormGroup();
  }

  onSubmitUpdate() {
    this.submitted = true;
    if (this.dishForm.valid) {
      const dishAdd: Dish = new Dish(Math.round((this.dishForm.controls.price.value as number) * 100),
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
      id: this.formBuilder.control<number | null>(this.dish.id),
      name: this.formBuilder.control<string>(this.dish.name, Validators.required),
      price: this.formBuilder.control<number>(this.dish.price / 100, [Validators.required, Validators.min(0)]),
      category: this.formBuilder.control<string>(this.dish.category, Validators.required)
    });
  }

}
