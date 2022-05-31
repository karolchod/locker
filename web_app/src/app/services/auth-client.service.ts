import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { temporaryAllocator } from '@angular/compiler/src/render3/view/util';


@Injectable({
  providedIn: 'root'
})
export class AuthClientService {


  private tokenRequestResponse!: TokenRequestResponse;
  private userRequestResponse!: UserRequestResponse;

  private subjectName = new Subject<any>(); //need to create a subject

  constructor(private httpClient: HttpClient) {

  }

  public login(username: string, password: string): Observable<TokenRequestResponse> {


    const formData = new FormData();


    // append your data
    formData.append('username', username);
    formData.append('password', password);

    const responseObservable = this.httpClient.post<TokenRequestResponse>('http://localhost:8080/api/login', formData);

    responseObservable.subscribe(value => {

      this.tokenRequestResponse = value;

      const httpOptions = {
        headers: new HttpHeaders({
          // 'Content-Type':  'application/json',
          Authorization: 'Bearer ' + this.tokenRequestResponse.access_token
        })
      };
      this.httpClient.get<UserRequestResponse>('http://localhost:8080/api/user/bytoken', httpOptions).subscribe(value2 => {

        this.userRequestResponse = value2;
        this.sendUpdate("Updated")
      });
    });

    return responseObservable;
  }

  public getToken(): string {
    if (this.tokenRequestResponse != null) {
      return this.tokenRequestResponse.access_token;
    } else {
      return '';
    }
  }

  public getLoggedUserName(): string {
    console.log(this.userRequestResponse)
    if (this.userRequestResponse != null) {
      return this.userRequestResponse.name;
    } else {
      return '';
    }
  }




  sendUpdate(message: string) { //the component that wants to update something, calls this fn
    this.subjectName.next({ text: message }); //next() will feed the value in Subject
  }

  getUpdate(): Observable<any> { //the receiver component calls this function 
    return this.subjectName.asObservable(); //it returns as an observable to which the receiver funtion will subscribe
  }
}



export interface TokenRequestResponse {
  access_token: string;
  refresh_token: string;
  user_role: string;
}

export interface UserRequestResponse {
  id: number;
  name: string;
  username: string;
  password: string;
  role_id: number;
}

