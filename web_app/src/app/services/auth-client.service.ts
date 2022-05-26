import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthClientService {

  constructor(private httpClient: HttpClient) {

  }

  public login(username:string,password:string): Observable<TokenRequestResponse> {


    const formData = new FormData();

// append your data
formData.append('username', username);
formData.append('password', password);
    

    return this.httpClient.post<TokenRequestResponse>('http://localhost:8080/api/login',formData);
  }

}

export interface TokenRequestResponse {
  access_token: string;
  refresh_token: string;
  user_role: string;
}

