import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthClientService } from 'src/app/services/auth-client.service';

@Component({
  selector: 'app-main-list',
  templateUrl: './main-list.component.html',
  styleUrls: ['./main-list.component.css']
})
export class MainListComponent implements OnInit {
  private subscriptionName: Subscription;
  show = false
  constructor(private authClientService: AuthClientService) {
    this.subscriptionName = this.authClientService.getUpdate().subscribe
      (message => { //message contains the data sent from service
        this.show = true
      });
  }

  ngOnInit(): void {
  }

}
