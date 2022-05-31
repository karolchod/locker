import { Component, OnInit } from '@angular/core';
import { AuthClientService } from 'src/app/services/auth-client.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-app-top-bar',
  templateUrl: './app-top-bar.component.html',
  styleUrls: ['./app-top-bar.component.css']
})
export class AppTopBarComponent implements OnInit {

  private subscriptionName: Subscription;

  imageSrc = 'assets/setting.png';
  imageAlt = 'Settings';
  login =this.authClientService.getLoggedUserName()
  show = false

  constructor(private authClientService: AuthClientService) {
    this.subscriptionName= this.authClientService.getUpdate().subscribe
             (message => { //message contains the data sent from service
              this.login =this.authClientService.getLoggedUserName()
              this.show = true
             });
  }

  ngOnInit(): void {
  }



}
