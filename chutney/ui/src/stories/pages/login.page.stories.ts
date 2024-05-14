import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { LoginComponent } from '@core/components/login/login.component';
import { InfoService, LoginService } from '@core/services';
import { Authorization, User } from '@model';
import { intersection } from '@shared/tools';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AlertService } from '@shared';
import { FormsModule } from '@angular/forms';

const mockLoginService = {
  hasAuthorization(
    authorization: Array<Authorization> | Authorization = [],
    u: User = null,
  ) {
    return (
      !authorization.length ||
      intersection([Authorization.SCENARIO_EXECUTE], [...authorization]).length
    );
  },
  isAuthenticated(): boolean {
    return false;
  },
};

const mockInfoService = {
  getVersion(): Observable<string> {
    return of("fake.version");
  },
  getApplicationName(): Observable<string> {
    return of("app-name");
  },
};

export default {
  title: "Pages/Login",
  component: LoginComponent,
  decorators: [
    moduleMetadata({
      imports: [
          FormsModule
      ],
      providers: [
        { provide: LoginService, useValue: mockLoginService },
        { provide: InfoService, useValue: mockInfoService },
        {
          provide: ActivatedRoute,
          useValue: {
            params: of([{ action: "login" }]),
            queryParams: of([{ url: "/" }]),
          },
        },
          {provide: AlertService, useValue: {}}
      ],
    }),
  ],
  args: {
    applicationName: "Chutney Instance App Name",
    connectionError: "",
    version: "1.0.0-RELEASE",
  },
} as Meta;

type Story = StoryObj<LoginComponent>;

export const Default: Story = {};

export const Error: Story = {
  args: {
    ...Default.args,
    connectionError: "this is an example error message",
  },
};

export const LongValues: Story = {
  args: {
    ...Default.args,
    applicationName:
      "This is a very long example of an application name, This is a very long example of an application name",
    connectionError:
      "This is a very long example of an error message, This is a very long example of an error message, This is a very long example of an error message, " +
      "This is a very long example of an error message, This is a very long example of an error message, This is a very long example of an error message",
    version: "This is a very long example of a version",
  },
};
