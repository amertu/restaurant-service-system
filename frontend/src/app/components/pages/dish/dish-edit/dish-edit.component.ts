import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Dish} from '../../../../dtos/dish';
import {DishService} from '../../../../services/dish.service';
import {AlertService} from '../../../../services/alert.service';
import {NgIf} from '@angular/common';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ApplicationUser} from '../../../../dtos/application-user';

@Component({
  selector: 'app-dish-edit',
  templateUrl: './dish-edit.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  styleUrls: ['./dish-edit.component.scss']
})
export class DishEditComponent implements OnInit, OnChanges {
  @Input() dish: any;
  submitted: boolean = false;

  constructor(private dishService: DishService,
              private formBuilder: FormBuilder,
              private alertService: AlertService,
              public activeModal: NgbActiveModal) {
  }

  protected dishForm: FormGroup;

  ngOnInit() {
    this.initDishFormGroup();
  }

  ngOnChanges(): void {
    this.initDishFormGroup();
  }

  private initDishFormGroup() {
    if (!this.dish) {
      return;
    }
    this.dishForm = this.formBuilder.group<{
      id: FormControl<number | null>;
      name: FormControl<string>;
      price: FormControl<number>;
      category: FormControl<string>;
    }>({
      id: this.formBuilder.control<number | null>(this.dish.id),
      name: this.formBuilder.control<string>(this.dish.name, Validators.required),
      price: this.formBuilder.control<number>(this.dish.price / 100, [Validators.required, Validators.min(0)]),
      category: this.formBuilder.control<string>(this.dish.category, Validators.required)
    });
  }

  onSubmitUpdate() {
    this.submitted = true;
    if (this.dishForm.valid) {
      const updatedValues = {
        name: this.dishForm.controls.name.value,
        price: Math.round(this.dishForm.controls.price.value * 100),
        category: this.dishForm.controls.category.value
      };
      const updatedDish : Dish = {
        ...this.dish,
        ...updatedValues
      }
      this.dishService.updateDish(updatedDish).subscribe({
        next: () => {
          window.location.reload();
          this.activeModal.close();
        },
        error: (error) => {
          this.alertService.error(error);
        }
      })
      ;
    }
  }
}
