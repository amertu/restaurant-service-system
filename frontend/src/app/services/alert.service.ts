import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AlertContentForNgbModalComponent } from '../components/alert/alert-content-for-ngb-modal/alert-content-for-ngb-modal.component';




@Injectable({
  providedIn: 'root'
})
export class AlertService {
  public successFlag = false;
  public successMessage = '';
  public errorFlag = false;
  public errorMessage = '';
  public warningFlag = false;
  public warningMessage = '';
  public infoFlag = false;
  public infoMessage = '';
  private referenceToModal: NgbModalRef;

  constructor(private router: Router, private modalService: NgbModal) {
    router.events.subscribe(
      () => {
        this.vanishAll();
      }
    );
  }

  vanishAll() {
    this.successFlag = false;
    this.errorFlag = false;
    this.warningFlag = false;
    this.infoFlag = false;
    if (this.referenceToModal) {
      this.referenceToModal.close();
    }
  }

  vanishSuccess() {
    this.successFlag = false;
  }

  vanishError() {
    this.errorFlag = false;
  }

  vanishWarning() {
    this.warningFlag = false;
  }

  vanishInfo() {
    this.infoFlag = false;
  }
  reportSuccess(message: string) {
    console.log(message);
    this.successFlag = true;
    this.successMessage = message;
    window.scrollTo(0, 0);
  }

  reportSuccessModal(message: string) {
    this.referenceToModal = this.modalService.open(AlertContentForNgbModalComponent);
    this.referenceToModal.componentInstance.title = 'Success';
    this.referenceToModal.componentInstance.message = message;
  }

  reportError(message: string) {
    console.log(message);
    this.errorFlag = true;
    this.errorMessage = message;
    window.scrollTo(0, 0);
  }



  reportErrorModal(error) {
    const message = this.getErrorMessage(error);
    this.referenceToModal = this.modalService.open(AlertContentForNgbModalComponent);
    this.referenceToModal.componentInstance.title = 'Error';
    this.referenceToModal.componentInstance.message = message;
  }
  
  reportErrorMessageModal(message: string) {
    this.referenceToModal = this.modalService.open(AlertContentForNgbModalComponent);
    this.referenceToModal.componentInstance.title = 'Error';
    this.referenceToModal.componentInstance.message = message;
  }

  reportWarning(message: string) {
    console.log(message);
    this.warningFlag = true;
    this.warningMessage = message;
    window.scrollTo(0, 0);
  }

  reportInfo(message: string) {
    console.log(message);
    this.infoFlag = true;
    this.infoMessage = message;
    window.scrollTo(0, 0);
  }


  error(error: any) {
    console.log(error);
    this.errorFlag = true;
    this.errorMessage = this.getErrorMessage(error);
    window.scrollTo(0, 0);
  }

  getErrorMessage(error: any): string {
    console.log('getErrorMessage');
    console.log(error);
    
    if (error.status === 0) {
      // If status is 0, the backend is probably down
      console.log('If status is 0, the backend is probably down');
      return 'The backend seems not to be reachable';
    } else if (error.error.message === undefined || error.error.message === 'No message available') {
      // If no detailed error message is provided, fall back to the simple error name
      console.log('If no detailed error message is provided, fall back to the simple error name');
      return error.error;
    } else {
      console.log('Error message is provided: ' + error.error.message);
      return error.error.message;
    }
  }
}



