import { Component, OnInit } from '@angular/core';
import { AuthClientService, TokenRequestResponse } from 'src/app/services/auth-client.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  // tokenRequestResponse: TokenRequestResponse | undefined;
  token: string | undefined;
  constructor(private authClientService: AuthClientService) { }

  ngOnInit(): void {

  }

  tryLogin(username: string, password: string) {
    this.authClientService.login(username, password).subscribe(value => {
      this.token=value.access_token;
    });
  }

  loggedIn():boolean{
    return this.authClientService.getToken().length>0;
  }



}
