export class ApplicationUser {
  constructor(
    public id: number,
    public email: string,
    public password: string,
    public firstName: string,
    public lastName: string,
    public ssnr: number,
    public admin: boolean,
    public blocked: boolean
  ) {}
}
