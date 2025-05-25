import {Component, OnInit} from '@angular/core';
import {AlertService} from '../../../../services/alert.service';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DishService} from '../../../../services/dish.service';
import {Dish} from '../../../../dtos/dish';
import {NgIf} from '@angular/common';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-dish-add',
  templateUrl: './dish-add.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  styleUrls: ['./dish-add.component.scss']
})
export class DishAddComponent implements OnInit {
  submitted: boolean = false;
  protected dishForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private dishService: DishService,
              private alertService: AlertService,
              public activeModal: NgbActiveModal) {
  }


  ngOnInit(): void {
    this.initDishFormGroup();
  }

  private initDishFormGroup() {
    this.dishForm = this.formBuilder.group<{
      name: FormControl<string>;
      price: FormControl<number>;
      category: FormControl<string>;

    }>({
      name: this.formBuilder.control<string>('', [Validators.required]),
      price: this.formBuilder.control<number>(0, [Validators.required, Validators.min(0)]),
      category: this.formBuilder.control<string>('', [Validators.required])
    });
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
    this.dishService.createDish(dish).subscribe({
      next: () => {
        window.location.reload();
        this.activeModal.close();
      },
      error: (error) => this.alertService.error(error)
    });
  }
}
